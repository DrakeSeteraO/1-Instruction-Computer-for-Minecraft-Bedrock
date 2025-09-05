// MinecraftComputer.Compiler made by Drake Setera
package MinecraftComputer;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;


public class Compiler {
    // Global variables
    public boolean main = true;
    public boolean get_arr_used = false;
    public int cur_instruction_num = 0;
    public int main_instruction_num = 0;
    public int cur_variable_address_num = 255;
    public int mem_used = 0;
    public int[] RAM = new int[256];
    public char[] Composition = new char[256];
    public ArrayList<String> instructions = new ArrayList<>();
    public ArrayList<Integer> checkpoint_calls = new ArrayList<>();
    public HashMap<String, Integer> variable_location = new HashMap<>();
    public HashMap<String, Boolean> variables_enabled = new HashMap<>();
    public HashMap<String, String> default_variable_vals = new HashMap<>();
    public Required_vars default_vars_info = new Required_vars();



    public Compiler(String RAM_file){
        // Constructor
        create_zero_var();
        create_empty_composition();
        read_RAM_file(RAM_file);
        default_var_values();
    }


    private void default_var_values(){
        // Loads default variables into Hashmaps
        variables_enabled.put("temp", false);
        default_variable_vals.put("temp", "0");

        variables_enabled.put("temp2", false);
        default_variable_vals.put("temp2", "0");

        variables_enabled.put("temp3", false);
        default_variable_vals.put("temp3", "0");

        variables_enabled.put("arr.temp", false);
        default_variable_vals.put("arr.temp", "0");

        variables_enabled.put("arr.a", false);
        default_variable_vals.put("arr.a", "0");

        variables_enabled.put("arr.b", false);
        default_variable_vals.put("arr.b", "0");

        variables_enabled.put("arr.c", false);
        default_variable_vals.put("arr.c", "0");

        variables_enabled.put("arr.inv_ptr", false);
        default_variable_vals.put("arr.inv_ptr", "0");
    }


    private void read_RAM_file(String Code_file){
        // Set RAM to all 0's
        zero_RAM();

        try{

            // Read RAM file
            BufferedReader file = new BufferedReader(new FileReader(Code_file));

            // Update MinecraftComputer.Emulator RAM
            String line;
            int i = 0;
            String error_msg = "";
            int error_num = 0;

            // Update the RAM line by line
            while ((line = file.readLine()) != null){

                try {
                    // If i is a valid address in the RAM then update the RAM value
                    this.instructions.add(line);

                } catch (Exception why) { // If there is an error when trying to read the RAM file then add it to the error msg
                    error_num ++;
                    error_msg = error_msg + "\n\nThere was an error loading RAM on line: " + String.valueOf(i+1) +"\nTried to load RAM value: " + line;
                }

                // Increment i
                i++;
            }

            // Close RAM file and display errors
            file.close();
            System.out.println("\nThere were " + String.valueOf(error_num) +" errors when loading RAM\n" + error_msg);

        } catch (Exception ex){
            // Catch if there is an error opening file
            System.out.println("There was an error loading RAM file into MinecraftComputer.Emulator");
        }
    }


    private void zero_RAM(){
        // Sets all values in the RAM to zero
        for (int i = 0; i < 256; i++) {
            this.RAM[i] = 0;
        }
    }


    private void save_RAM_file(String file_name){
        // Saves RAM data to binary file that the MinecraftComputer.Emulator can run

        try {
            // Convert RAM to String for the output file
            String output = "";
            for (int i = 0; i < this.RAM.length; i ++){
                output += String.valueOf(this.RAM[i]) + "\n";
            }

            // Write output to file
            FileWriter file = new FileWriter(file_name);
            file.write(output);
            file.close();

        } catch (Exception ex){
            System.out.println("There was an error saving the RAM file");
        }
    }


    public void compile(String output_file_name){
        // Compiles assembly file to binary file
        do_instruction(0, 0);
        String[] command  = {"arr[16]", "screen", "0"};
        create_arr(command);
        update_default_var();
        perform_instructions();
        update_goto();
        save_RAM_file(output_file_name);
    }


    private void update_default_var(){
        // Detects if any default variables are being used,
        // if so it enables them
        for (int i = 0; i < this.instructions.size(); i++){
            String[] instruction_parts = get_instruction_parts(this.instructions.get(i));

            if (instruction_parts.length > 0 && !instruction_parts[0].contains("#") && !instruction_parts[0].equals("var") && !instruction_parts[0].startsWith("arr[")){
                create_vars(instruction_parts);
                for (int x = 1; x < instruction_parts.length; x ++){
                    insert_default_var(instruction_parts[x]);
                }
            } else if (instruction_parts.length > 0 && instruction_parts[0].startsWith("arr[")) {
                String[] info = {"var", "arr.temp", "0"};
                load_default_var(info);
            }
        }
    }


    private void insert_default_var(String var){
        // Inserts unused default variables into the RAM
        if (variables_enabled.containsKey(var) && ! variables_enabled.get(var)){
            variables_enabled.put(var, true);
            String[] parts = {"var", var, default_variable_vals.get(var)};
            create_var(parts);
        } else if (var.matches("^-?[0-9]+$") && (variable_location.get(var) == null)) {
            variables_enabled.put(var, true);
            String[] parts = {"var", var, var};
            create_var(parts);
        } else if (var.contains("[")) {
            String[] command = {"arr.set", "0", "0"};
            create_vars(command);
            String[] command2 = {"arr.get", "0", "0"};
            create_vars(command2);

            String value = var.replace("]", "").split("\\[")[1];
            if (value.matches("^-?[0-9]+$") && (variable_location.get(value) == null)) {
                variables_enabled.put(value, true);
                String[] parts = {"var", value, value};
                create_var(parts);
            }
        }
    }


    private void create_vars(String[] parts){
        // Determines what default variables should be enabled to run current instruction
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();


        if (default_vars_info.test_if_requires_variables(parts[0])) {
            names = default_vars_info.get_var_names(parts[0]);
            values = default_vars_info.get_var_values(parts[0]);
        }

        for (int i = 0; i < names.size(); i++){
            String[] info = {"var", names.get(i), values.get(i)};
            load_default_var(info);
        }
    }


    private void load_default_var(String[] values){
        // Updates default variable information and status in te compiler
        if (variables_enabled.get(values[1]) == null || !variables_enabled.get(values[1])){
            create_var(values);
            variables_enabled.put(values[1], true);
        }
    }


    private void perform_instructions(){
        // Iterates through the assembly instructions and updates RAM
        for (int i = 0; i < this.instructions.size(); i++){
            String[] instruction_parts = get_instruction_parts(this.instructions.get(i));

            if (instruction_parts.length > 0 && !instruction_parts[0].contains("#")){
                do_specific_instruction(instruction_parts);
            }
        }

        output_data();
    }


    private void output_data() {
        System.out.println(String.valueOf(mem_used) + "/256, " + String.valueOf(((float) mem_used / 256) * 100) + "% Space used.");
        String output = "[";
        for (int i = 0; i < 256; i++) {
            output += Composition[i];
        }
        System.out.println(output+"]");
    }


    private String[] get_instruction_parts(String instruction){
        // Converts current instruction into its arguments
        String[] parts = instruction.split(" ");
        ArrayList<String> required_parts = new ArrayList<>();

        for (int i = 0; i < parts.length; i++){
            if (parts[i].length() > 0){
                required_parts.add(parts[i]);
            }
        }

        return required_parts.toArray(new String[0]);
    }


    private void do_specific_instruction(String[] parts){
        // Determines which instruction to perform
        if (parts[0].equals("var")){
            create_var(parts);
        } else if (parts[0].equals("zero")) {
            do_zero(parts);
        } else if (parts[0].equals("sub")) {
            do_sub(parts);
        } else if (parts[0].equals("add")) {
            do_add(parts);
        } else if (parts[0].equals("set")) {
            if (!parts[1].equals(parts[2])){
                do_set(parts);
            }
        } else if (parts[0].equals("stop")) {
            do_stop(parts);
        } else if (parts[0].equals("neg")) {
            do_neg(parts);
        } else if (parts[0].equals("not")) {
            do_not(parts);
        } else if (parts[0].equals("abs")) {
            do_abs(parts);
        } else if (parts[0].equals("mult")) {
            do_mult(parts);
        } else if (parts[0].equals("mult+")) {
            do_mult_plus(parts);
        }else if (parts[0].equals("cp")) {
            do_cp(parts);
        } else if (parts[0].equals("goto")) {
            do_goto(parts);
        } else if (parts[0].equals("shiftl")) {
            do_shiftl(parts);
        } else if (parts[0].equals("shiftr")) {
            do_shiftr(parts);
        } else if (parts[0].equals("wait")) {
            do_wait(parts);
        } else if (parts[0].equals("pow+")) {
            do_pow_plus(parts);
        } else if (parts[0].startsWith("arr[")) {
            create_arr(parts);
        } else if (parts[0].equals("arr.get")) {
            do_arr_get(parts);
        } else if (parts[0].equals("arr.set")) {
            do_arr_set(parts);
        }
    }


    private void create_zero_var(){
        // Creates a new variable
        variable_location.put("0", 0);
        mem_used ++;
    }


    private void create_empty_composition(){
        // Creates a new variable
        for (int i = 0; i < 256; i++) {
            Composition[i] = '_';
        }
    }


    private void create_var(String[] parts){
        // Creates a new variable
        if (!variable_location.containsKey(parts[1])) {
            variable_location.put(parts[1], cur_variable_address_num);

            if (parts.length <= 2) {
                RAM[cur_variable_address_num] = 0;
            } else {
                RAM[cur_variable_address_num] = Integer.parseInt(parts[2]);
            }

            Composition[cur_variable_address_num] = '@';
            cur_variable_address_num--;
            mem_used++;
        }
    }


    private void do_zero(String[] parts){
        // Sets variable_A = 0
        // Designed by Drake Setera

        int A_address = variable_location.get(parts[1]);
        do_instruction(A_address, A_address);
        mem_used ++;
    }


    private void do_sub(String[] parts){
        if (parts.length == 3) {
            // Sets variable_A = variable_A - variable_B
            // Designed by Oakleypaws

            int A_address = get_address(parts[1]);
            int B_address = get_address(parts[2]);

            do_instruction(A_address, B_address);
            set_address(parts[1]);
            mem_used++;

        } else if (parts.length == 4){
            // Sets variable_C = variable_A - variable_B
            // Designed by Oakleypaws

            int A_address = get_address(parts[1]);
            int B_address = get_address(parts[2]);
            int C_address = get_address(parts[3]);
            int zero_address = variable_location.get("0");

            do_instruction(C_address, C_address);
            do_instruction(zero_address, A_address);
            do_instruction(C_address, zero_address);
            do_instruction(C_address, B_address);
            do_instruction(zero_address, zero_address);
            set_address(parts[3]);
            mem_used += 5;
        }
    }


    private void do_add(String[] parts){
        if (parts.length == 3){
            // Sets variable_A = variable_A + variable_B
            // Designed by Oakleypaws
            int A_address = get_address(parts[1]);
            int B_address = get_address(parts[2]);
            int zero_address = variable_location.get("0");

            do_instruction(zero_address, B_address);
            do_instruction(A_address, zero_address);
            do_instruction(zero_address, zero_address);
            set_address(parts[1]);
            mem_used += 3;

        } else if (parts.length == 4) {
            // Sets variable_C = variable_A + variable_B
            // Designed by Oakleypaws
            int A_address = get_address(parts[1]);
            int B_address = get_address(parts[2]);
            int C_address = get_address(parts[3]);
            int zero_address = variable_location.get("0");

            do_instruction(zero_address, A_address);
            do_instruction(zero_address, B_address);
            do_instruction(C_address, C_address);
            do_instruction(C_address, zero_address);
            do_instruction(zero_address, zero_address);
            set_address(parts[3]);
            mem_used += 5;
        }
    }


    private void do_set(String[] parts){
        // Sets variable_A = variable_B
        // Designed by Oakleypaws


            int A_address = get_address(parts[1]);
            int B_address = get_address(parts[2]);
            int zero_address = variable_location.get("0");

            do_instruction(zero_address, B_address);
            do_instruction(A_address, A_address);
            do_instruction(A_address, zero_address);
            do_instruction(zero_address, zero_address);
            set_address(parts[1]);
            mem_used += 4;
    }


    private void do_stop(String[] parts){
        // Ends the program
        // Designed by Oakleypaws
        int zero_address = variable_location.get("0");

        do_instruction(zero_address, zero_address, cur_instruction_num, cur_instruction_num);
        mem_used ++;
    }


    private void do_neg(String[] parts){
        // Sets variable_A = -variable_A
        // Designed by Oakleypaws
        int A_address = get_address(parts[1]);
        int zero_address = variable_location.get("0");
        int temp_address = variable_location.get("temp");

        do_instruction(zero_address, A_address);
        do_instruction(temp_address, zero_address);
        do_instruction(A_address, A_address);
        do_instruction(A_address, temp_address);
        do_instruction(zero_address, zero_address);
        do_instruction(temp_address, temp_address);
        set_address(parts[1]);
        mem_used += 6;
    }


    private void do_not(String[] parts){
        if (parts.length == 2) {
            // Sets variable_A = variable_A!
            // Designed by Oakleypaws

            int A_address = get_address(parts[1]);
            int zero_address = variable_location.get("0");
            int temp_address = variable_location.get("temp");
            int one_address = variable_location.get("1");

            do_instruction(zero_address, A_address);
            do_instruction(temp_address, zero_address);
            do_instruction(A_address, A_address);
            do_instruction(A_address, temp_address);
            do_instruction(A_address, one_address);
            do_instruction(zero_address, zero_address);
            do_instruction(temp_address, temp_address);
            set_address(parts[1]);
            mem_used += 7;

        } else if (parts.length == 3) {
            // Sets variable_B = variable_A!
            // Designed by Oakleypaws

            int A_address = get_address(parts[1]);
            int B_address = get_address(parts[2]);
            int one_address = variable_location.get("1");

            do_instruction(B_address, B_address);
            do_instruction(B_address, A_address);
            do_instruction(B_address, one_address);
            set_address(parts[2]);
            mem_used += 3;
        }
    }


    private void do_abs(String[] parts){
        if (parts.length == 2) {
            // Sets variable_A = absolute_value(variable_A)
            // Designed by Oakleypaws

            int A_address = get_address(parts[1]);
            int zero_address = variable_location.get("0");
            int temp_address = variable_location.get("temp");

            do_instruction(A_address, zero_address, cur_instruction_num + 1, cur_instruction_num + 7);
            do_instruction(zero_address, A_address);
            do_instruction(temp_address, zero_address);
            do_instruction(A_address, A_address);
            do_instruction(A_address, temp_address);
            do_instruction(zero_address, zero_address);
            do_instruction(temp_address, temp_address);
            set_address(parts[1]);
            mem_used += 7;

        } else if (parts.length == 3){
            // Sets variable_B = absolute_value(variable_A)
            // Designed by Oakleypaws

            int A_address = get_address(parts[1]);
            int B_address = get_address(parts[2]);
            int zero_address = variable_location.get("0");

            do_instruction(B_address, B_address);
            do_instruction(B_address, A_address, cur_instruction_num + 1, cur_instruction_num + 7);
            do_instruction(zero_address, A_address);
            do_instruction(B_address, B_address);
            do_instruction(B_address, zero_address);
            do_instruction(zero_address, zero_address);
            set_address(parts[2]);
            mem_used += 6;
        }
    }


    private void do_mult(String[] parts){
        if (parts.length == 3){
            // Sets variable_A = variable_A * variable_B
            // Designed by BratWORST

            int A_address = get_address(parts[1]);
            int B_address = get_address(parts[2]);
            int zero_address = variable_location.get("0");
            int temp_address = variable_location.get("temp");
            int temp2_address = variable_location.get("temp2");
            int one_address = variable_location.get("1");
            int neg1_address = variable_location.get("-1");

            do_instruction(zero_address, B_address,cur_instruction_num+1,cur_instruction_num+3);

            do_instruction(temp_address,A_address);
            do_instruction(zero_address,neg1_address,cur_instruction_num-1, cur_instruction_num+6);

            do_instruction(temp2_address, A_address, cur_instruction_num + 2, cur_instruction_num + 2);
            do_instruction(temp_address,temp2_address);
            do_instruction(zero_address,one_address, cur_instruction_num + 1, cur_instruction_num - 1);

            do_instruction(zero_address,zero_address);
            do_instruction(temp2_address,temp2_address);
            do_instruction(A_address, A_address);
            do_instruction(A_address,temp_address);
            do_instruction(temp_address,temp_address);
            set_address(parts[1]);
            mem_used += 11;

        } else if (parts.length == 4){
            // Sets variable_C = variable_A * variable_B
            // Designed by BratWORST

            int A_address = get_address(parts[1]);
            int B_address = get_address(parts[2]);
            int C_address = get_address(parts[3]);
            int zero_address = variable_location.get("0");
            int temp_address = variable_location.get("temp");
            int temp2_address = variable_location.get("temp2");
            int one_address = variable_location.get("1");
            int neg_address = variable_location.get("-1");

            do_instruction(zero_address, B_address,cur_instruction_num+1,cur_instruction_num+3);

            do_instruction(temp_address,A_address);
            do_instruction(zero_address,neg_address,cur_instruction_num-1, cur_instruction_num+6);

            do_instruction(temp2_address, A_address, cur_instruction_num + 2, cur_instruction_num + 2);
            do_instruction(temp_address,temp2_address);
            do_instruction(zero_address,one_address, cur_instruction_num + 1, cur_instruction_num - 1);

            do_instruction(zero_address,zero_address);
            do_instruction(temp2_address,temp2_address);
            do_instruction(C_address, C_address);
            do_instruction(C_address,temp_address);
            do_instruction(temp_address,temp_address);
            set_address(parts[3]);
            mem_used += 11;
        }
    }


    private void do_mult_plus(String[] parts){
        if (parts.length == 3){
            // Sets variable_A = variable_A * variable_B
            // Designed by Oakleypaws

            int A_address = get_address(parts[1]);
            int B_address = get_address(parts[2]);
            int zero_address = variable_location.get("0");
            int one_address = variable_location.get("1");
            int neg_address = variable_location.get("-1");

            do_instruction(A_address, one_address, cur_instruction_num + 2, cur_instruction_num + 1);
            do_instruction(zero_address, B_address, cur_instruction_num - 1, cur_instruction_num - 1);
            do_instruction(A_address, zero_address);
            do_instruction(A_address, neg_address);
            do_instruction(zero_address, zero_address);
            set_address(parts[1]);
            mem_used += 5;

        } else if (parts.length == 4){
            // Sets variable_C = variable_A * variable_B
            // Designed by Oakleypaws

            int A_address = get_address(parts[1]);
            int B_address = get_address(parts[2]);
            int C_address = get_address(parts[3]);
            int zero_address = variable_location.get("0");
            int neg_address = variable_location.get("-1");

            do_instruction(C_address, C_address);
            do_instruction(C_address, A_address);
            do_instruction(zero_address, B_address);
            do_instruction(C_address, neg_address, cur_instruction_num -1, cur_instruction_num + 1);
            do_instruction(C_address, zero_address);
            do_instruction(zero_address, zero_address);
            set_address(parts[3]);
            mem_used += 6;

        }
    }


    private void do_shiftl(String[] parts){
        if (parts.length == 2){
            // Sets variable_A = variable_A << 1
            // Designed by Oakleypaws

            int A_address = get_address(parts[1]);
            int zero_address = variable_location.get("0");

            do_instruction(zero_address, A_address);
            do_instruction(A_address, zero_address);
            do_instruction(zero_address, zero_address);
            set_address(parts[1]);
            mem_used += 3;

        } else if (parts.length == 3){
            // Sets variable_A = variable_A << variable_B
            // Designed by BratWORST

            int A_address = get_address(parts[1]);
            int B_address = get_address(parts[2]);
            int zero_address = variable_location.get("0");
            int temp_address = variable_location.get("temp");
            int neg_address = variable_location.get("-1");


            do_instruction(zero_address, B_address, cur_instruction_num + 1, cur_instruction_num + 5);
            do_instruction(temp_address, A_address);
            do_instruction(A_address, temp_address);
            do_instruction(temp_address, temp_address);
            do_instruction(zero_address, neg_address, cur_instruction_num - 3, cur_instruction_num + 1);
            set_address(parts[1]);
            mem_used += 5;
        }
    }


    private void do_shiftr(String[] parts){
        if (parts.length == 2){
            // Sets variable_A = variable_A >> 1
            // Designed by Oakleypaws

            int A_address = get_address(parts[1]);
            int zero_address = variable_location.get("0");
            int temp_address = variable_location.get("temp");
            int temp2_address = variable_location.get("temp2");
            int temp3_address = variable_location.get("temp3");
            int neg_address = variable_location.get("-1");
            int thirty_two_address = variable_location.get("32");

            do_instruction(temp3_address, thirty_two_address);
            do_instruction(temp2_address, temp_address);
            do_instruction(temp_address, temp2_address);
            do_instruction(temp2_address, temp2_address);
            do_instruction(zero_address, A_address, cur_instruction_num + 2, cur_instruction_num + 1);
            do_instruction(temp_address, neg_address);
            do_instruction(A_address, zero_address);
            do_instruction(zero_address, zero_address);
            do_instruction(temp3_address, neg_address, cur_instruction_num - 7, cur_instruction_num + 1);
            do_instruction(A_address, A_address);
            do_instruction(zero_address, temp_address);
            do_instruction(A_address, zero_address);
            do_instruction(zero_address, zero_address);
            do_instruction(temp_address, temp_address);
            set_address(parts[1]);
            mem_used += 14;

        } else if (parts.length == 3){
            // Sets variable_A = variable_A >> variable_B
            // Designed by Oakleypaws

            int A_address = get_address(parts[1]);
            int B_address = get_address(parts[2]);
            int zero_address = variable_location.get("0");
            int temp_address = variable_location.get("temp");
            int neg_address = variable_location.get("-1");
            int one_address = variable_location.get("1");
            int two_address = variable_location.get("1");


            do_instruction(temp_address, B_address);
            do_instruction(temp_address, neg_address, cur_instruction_num + 7, cur_instruction_num + 1);
            do_instruction(zero_address, one_address);
            do_instruction(A_address, two_address, cur_instruction_num + 1, cur_instruction_num - 1);
            do_instruction(zero_address, neg_address);
            do_instruction(A_address, A_address);
            do_instruction(A_address, zero_address);
            do_instruction(zero_address, zero_address, cur_instruction_num -6, cur_instruction_num -6);
            set_address(parts[1]);
            mem_used += 8;
        }
    }


    private void do_wait(String[] parts){
        // Waits variable_A number of instructions
        // Designed by Oakleypaws

        int A_address = get_address(parts[1]);
        int zero_address = variable_location.get("0");
        int neg_address = variable_location.get("-1");

        do_instruction(zero_address, A_address);
        do_instruction(zero_address, neg_address, cur_instruction_num, cur_instruction_num + 1);
        mem_used += 2;
    }


    private void do_pow_plus(String[] parts){
        if (parts.length == 3){
            // Sets variable_A = variable_A * variable_B
            // Designed by Oakleypaws

            int A_address = get_address(parts[1]);
            int B_address = get_address(parts[2]);
            int zero_address = variable_location.get("0");
            int one_address = variable_location.get("1");
            int neg_address = variable_location.get("-1");

            do_instruction(A_address, one_address, cur_instruction_num + 2, cur_instruction_num + 1);
            do_instruction(zero_address, B_address, cur_instruction_num - 1, cur_instruction_num - 1);
            do_instruction(A_address, zero_address);
            do_instruction(A_address, neg_address);
            do_instruction(zero_address, zero_address);
            set_address(parts[1]);
            mem_used += 5;

        } else if (parts.length == 4){
            // Sets variable_C = variable_A power of variable_B
            // Designed by Oakleypaws

            int A_address = get_address(parts[1]);
            int B_address = get_address(parts[2]);
            int C_address = get_address(parts[3]);
            int zero_address = variable_location.get("0");
            int temp_address = variable_location.get("temp");
            int temp2_address = variable_location.get("temp2");
            int neg_address = variable_location.get("-1");

            do_instruction(temp2_address, B_address);
            do_instruction(temp2_address, neg_address);
            do_instruction(zero_address, A_address);
            do_instruction(C_address, C_address);
            do_instruction(temp_address, A_address);
            do_instruction(C_address, zero_address);
            do_instruction(temp_address, neg_address, cur_instruction_num - 1, cur_instruction_num + 1);
            do_instruction(zero_address, zero_address);
            do_instruction(zero_address, C_address);
            do_instruction(temp2_address, neg_address, cur_instruction_num - 6, cur_instruction_num + 1);
            do_instruction(zero_address, zero_address);
            set_address(parts[3]);
            mem_used += 11;

        }
    }


    private void do_cp(String[] parts){
        // Sets a checkpoint that can be called later
        // Designed by Oakleypaws
        variable_location.put(parts[1], cur_instruction_num);
    }


    private void do_goto(String[] parts){
        // Goes back to the called checkpoint
        // Designed by Oakleypaws
        checkpoint_calls.add(cur_instruction_num - 1);
    }


    private void update_goto(){
        for (int i = 0; i < instructions.size(); i++){
            String[] parts = get_instruction_parts(this.instructions.get(i));
            if (parts.length > 0 && parts[0].equals("goto")){
                String check_point_name = parts[1];
                int new_location = variable_location.get(check_point_name);
                int location = checkpoint_calls.getFirst();
                checkpoint_calls.removeFirst();

                int instruction = this.RAM[location];
                int new_instruction = instruction - (instruction & 0xFFFF0000) + (new_location << 16) + (new_location << 24);
                this.RAM[location] = new_instruction;
            }
        }
    }

    private void create_arr(String[] parts) {
        // Creates an Array
        String[] command_parts = parts[0].split("\\[");
        int size = Integer.parseInt(command_parts[1].replace("]", ""));


        if (parts.length == 3 && parts[2].contains("{")){
            String[] values = parts[2].replace("{", "").replace("}", "").split(",");

            for (int i = 0; i < size; i++) {
                String temp = "0";

                if (i < values.length && !values[i].isEmpty()){
                    temp = values[i];
                }

                String[] command = {"var", parts[1] + "." + i, temp};
                create_var(command);
            }
        } else {
            String value = "0";
            if (parts.length == 3){
                value = parts[2];
            }

            for (int i = 0; i < size; i++) {
                String[] command = {"var", parts[1] + "." + i, value};
                create_var(command);
            }
        }

        String[] address_command = {"var", parts[1] + ".address", "0"};
        create_var(address_command);

        int address = variable_location.get(parts[1] + ".0");
        this.RAM[variable_location.get(parts[1] + ".address")] = address;
    }


    private void do_arr_get(String[] parts) {

            if (!get_arr_used) {
                create_arr_func();
                get_arr_used = true;
            }

            int value = (((cur_instruction_num + 3) << 16) + ((cur_instruction_num + 3) << 24));
            String[] command = {"var", String.valueOf(value), String.valueOf(value)};
            create_var(command);
            String[] command2 = {"var", String.valueOf(-value), String.valueOf(-value)};
            create_var(command2);

            int A_address = variable_location.get(parts[1]);
            int B_address = variable_location.get(parts[2]);
            int arr_A_address = variable_location.get("arr.a");
            int arr_B_address = variable_location.get("arr.b");
            int inv_return_address = variable_location.get(String.valueOf(-value));
            int fix_inv_return_address = variable_location.get(String.valueOf(value));
            int arr_return_address = variable_location.get("arr.get.return");
            int arr_start_address = variable_location.get("arr.get.start");

            // Sets arr parameters
            do_instruction(arr_A_address, A_address);
            do_instruction(arr_B_address, B_address);

            // Sets function return location and goes to function start
            do_instruction(arr_return_address, inv_return_address, arr_start_address, arr_start_address);

            // Resets function return location
            do_instruction(arr_return_address, fix_inv_return_address);
            mem_used += 4;
    }


    private void do_arr_set(String[] parts) {
        if (!get_arr_used) {
            create_arr_func();
            get_arr_used = true;
        }

        int value = (((cur_instruction_num + 4) << 16) + ((cur_instruction_num + 4) << 24));
        String[] command = {"var", String.valueOf(value), String.valueOf(value)};
        create_var(command);
        String[] command2 = {"var", String.valueOf(-value), String.valueOf(-value)};
        create_var(command2);

        int A_address = variable_location.get(parts[1]);
        int B_address = variable_location.get(parts[2]);
        int arr_A_address = variable_location.get("arr.a");
        int arr_B_address = variable_location.get("arr.b");
        int arr_C_address = variable_location.get("arr.c");
        int one_address = variable_location.get("1");
        int inv_return_address = variable_location.get(String.valueOf(-value));
        int fix_inv_return_address = variable_location.get(String.valueOf(value));
        int arr_return_address = variable_location.get("arr.get.return");
        int arr_start_address = variable_location.get("arr.get.start");

        // Sets arr parameters
        do_instruction(arr_A_address, A_address);
        do_instruction(arr_B_address, B_address);
        do_instruction(arr_C_address, one_address);

        // Sets function return location and goes to function start
        do_instruction(arr_return_address, inv_return_address, arr_start_address, arr_start_address);

        // Resets function return location
        do_instruction(arr_return_address, fix_inv_return_address);
        mem_used += 5;

    }


    private void create_arr_func(){
        int arr_A_address = variable_location.get("arr.a");
        int arr_B_address = variable_location.get("arr.b");
        int arr_temp_address = variable_location.get("arr.temp");
        int arr_inv_ptr_address = variable_location.get("arr.inv_ptr");
        int temp_address = variable_location.get("temp");
        int neg_address = variable_location.get("-1");
        int eight_address = variable_location.get("8");
        int temp2_address = variable_location.get("temp2");

        int temp3_address = variable_location.get("temp3");
        int arr_C_address = variable_location.get("arr.c");
        int zero_address = variable_location.get("0");

        main = false;
        cur_variable_address_num -= 27;
        cur_instruction_num = cur_variable_address_num + 1;
        variable_location.put("arr.get.start", cur_instruction_num);

        // Determines address in computer
        do_instruction(arr_A_address, arr_B_address);
        do_instruction(temp3_address, arr_A_address);

        // Shifts address left 8 bits
        do_instruction(temp_address, eight_address);
        do_instruction(temp2_address, arr_A_address);
        do_instruction(arr_A_address, temp2_address);
        do_instruction(temp2_address, temp2_address);
        do_instruction(temp_address, neg_address, cur_instruction_num - 3, cur_instruction_num + 1);

        // Modifies instruction to call address
        do_instruction(cur_instruction_num + 3, arr_A_address);

        // Gets negative of value you want to set
        do_instruction(temp2_address, arr_temp_address);

        // Resets variable
        do_instruction(arr_temp_address, arr_temp_address);

        // Gets value at address
        do_instruction(temp_address, 0); // Instruction that is getting modified
        do_instruction(arr_temp_address, temp_address);

        // Determines if the function is getting a value from the array, or also setting to the array
        do_instruction(arr_C_address, zero_address, cur_instruction_num + 1, cur_instruction_num + 8);

        // Set value
        do_instruction(zero_address, temp3_address);
        do_instruction(cur_instruction_num + 2, zero_address);
        do_instruction(temp2_address, temp_address);
        do_instruction(0, temp2_address);

        // Repair set value
        do_instruction(cur_instruction_num - 1, temp3_address);

        // Resets variables
        do_instruction(zero_address, zero_address);
        do_instruction(arr_C_address, arr_C_address);

        do_instruction(arr_A_address, arr_A_address);
        do_instruction(arr_B_address, arr_B_address);
        do_instruction(temp_address, temp_address);
        do_instruction(temp2_address, temp2_address);
        do_instruction(temp3_address, temp3_address);

        // Repairs get value
        do_instruction(cur_instruction_num - 15, cur_instruction_num - 15);
        do_instruction(cur_instruction_num - 16, arr_inv_ptr_address, 0, 0);
        mem_used += 27;

        RAM[arr_inv_ptr_address] = -combine_ptr(temp_address, 0, cur_instruction_num -16, cur_instruction_num - 16);
        variable_location.put("arr.get.return", cur_instruction_num - 1);
        main = true;
        cur_instruction_num = main_instruction_num;
    }


    private int get_address(String var_name) {
        if (!var_name.contains("[")) {
            return variable_location.get(var_name);
        } else {
            String[] parts = var_name.replace("]", "").split("\\[");

            if (parts[1].matches("^[0-9]+$")) {
                return variable_location.get(parts[0] + "." + parts[1]);

            } else {
                String[] command = {"arr.get", parts[0] + ".address", parts[1]};
                do_arr_get(command);
                return variable_location.get("arr.temp");
            }
        }
    }


    private void set_address(String var_name) {
        if (var_name.contains("[")) {
            String[] parts = var_name.replace("]", "").split("\\[");

            if (!parts[1].matches("^[0-9]+$")) {
                String[] command = {"arr.set", parts[0] + ".address", parts[1]};
                do_arr_set(command);
            }
        }
    }


    private void do_instruction(int A, int B){
        do_instruction(A, B, cur_instruction_num + 1, cur_instruction_num + 1);
    }


    private void do_instruction(int A, int B, int C, int D){
        int instruction = combine_ptr(A, B, C, D);
        RAM[cur_instruction_num] = instruction;
        cur_instruction_num ++;

        if (main){
            Composition[cur_instruction_num-1] = '#';
            main_instruction_num ++;
        } else {
            Composition[cur_instruction_num-1] = '$';
        }
    }


    private int combine_ptr(int A, int B, int C, int D){
        int output = (D << 24);
        output += (C << 16);
        output += (B << 8);
        output += A;

        return output;
    }


    public String get_string_RAM(){
        String output = "";
        for (int i = 0; i < RAM.length; i++){
            output += String.valueOf(RAM[i]) + "\n";
        }

        return output;
    }
}
