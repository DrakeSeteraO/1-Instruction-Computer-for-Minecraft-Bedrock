// MinecraftComputer.Emulator made by Drake Setera
package MinecraftComputer;


import java.io.BufferedReader;
import java.io.FileReader;


public class Emulator {
    // Global variables
    public int[] RAM = new int[256];
    private int[] end_RAM = new int[256];
    private long start_time = 0;
    private long run_time = 0;
    public int instruction_ptr = 0;


    public Emulator(String RAM_file){
        // Constructor
        read_RAM_file(RAM_file);
    }


    private void read_RAM_file(String RAM_file){
        // Set RAM to all 0's
        zero_RAM();

        try{

            // Read RAM file
            BufferedReader file = new BufferedReader(new FileReader(RAM_file));

            // Update MinecraftComputer.Emulator RAM
            String line;
            int i = 0;
            String error_msg = "";
            int error_num = 0;

            // Update the RAM line by line
            while ((line = file.readLine()) != null){

                try {
                    // If i is a valid address in the RAM then update the RAM value
                    if (i < 256) {
                        this.RAM[i] = Integer.parseInt(line);
                    }

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


    private String[] blank_Bits(){
        // Sets all the String's in the array to ""
        String[] output = new String[32];
        for (int i = 0; i < 32; i++) {
            output[i] = "";
        }
        return output;
    }


    public void run(){
        // Runs the MinecraftComputer.Emulator with 0 parameters
        this.start_time = System.nanoTime();
        run_computer(true, true, 1000000, false);
        this.run_time = System.nanoTime() - this.start_time;
    }


    public void run(boolean display_screen){
        // Runs the MinecraftComputer.Emulator with 1 parameters
        this.start_time = System.nanoTime();
        run_computer(display_screen, true, 1000000, false);
        this.run_time = System.nanoTime() - this.start_time;
    }


    public void run(boolean display, boolean display_end){
        // Runs the MinecraftComputer.Emulator with 2 parameters
        this.start_time = System.nanoTime();
        int[] val = run_computer(display, true, 1000000, false);
        if (display_end){
            display_screen(val);
        }
        this.run_time = System.nanoTime() - this.start_time;
    }


    public void run(boolean display, boolean display_end, boolean display_instruction_num){
        // Runs the MinecraftComputer.Emulator with 3 parameters
        this.start_time = System.nanoTime();
        int[] val = run_computer(display, display_instruction_num, 1000000, false);
        if (display_end){
            display_screen(val);
        }
        this.run_time = System.nanoTime() - this.start_time;
    }


    public void run(boolean display, boolean display_end, boolean display_instruction_num, boolean display_tot_instructions){
        // Runs the MinecraftComputer.Emulator with 4 parameters
        this.start_time = System.nanoTime();
        int[] val = run_computer(display, display_instruction_num, 1000000, display_tot_instructions);
        if (display_end){
            display_screen(val);
        }
        this.run_time = System.nanoTime() - this.start_time;
    }


    public void step(){
        // Runs the MinecraftComputer.Emulator with 0 parameters
        this.start_time = System.nanoTime();
        run_computer(true, true, 1, false);
        this.run_time = System.nanoTime() - this.start_time;
    }


    public void step(int amount){
        // Runs the MinecraftComputer.Emulator with 0 parameters
        this.start_time = System.nanoTime();
        run_computer(true, true, amount, false);
        this.run_time = System.nanoTime() - this.start_time;
    }


    public void step(int amount, boolean display_screen){
        // Runs the MinecraftComputer.Emulator with 1 parameters
        this.start_time = System.nanoTime();
        run_computer(display_screen, true, amount, false);
        this.run_time = System.nanoTime() - this.start_time;
    }


    public void step(int amount, boolean display, boolean display_end){
        // Runs the MinecraftComputer.Emulator with 2 parameters
        this.start_time = System.nanoTime();
        int[] val = run_computer(display, true, amount, false);
        if (display_end){
            display_screen(val);
        }
        this.run_time = System.nanoTime() - this.start_time;
    }


    public void step(int amount, boolean display, boolean display_end, boolean display_instruction_num){
        // Runs the MinecraftComputer.Emulator with 3 parameters
        this.start_time = System.nanoTime();
        int[] val = run_computer(display, display_instruction_num, amount, false);
        if (display_end){
            display_screen(val);
        }
        this.run_time = System.nanoTime() - this.start_time;
    }


    public long run_time_ns(){
        return this.run_time;
    }


    public long run_time_ms(){
        return this.run_time / 1000000;
    }


    private int[] run_computer(boolean display_screen, boolean display_instruction_num, int steps, boolean display_tot_instructions){
        // Create initial variables
        int[] RAM = this.RAM;
        int instruction_ptr = this.instruction_ptr;
        int repeat_amount = 0;
        int previous_instruction_ptr = 0;
        int instruction = RAM[instruction_ptr];

        // Loop through instructions
        boolean go = true;
        int cur_step = 0;
        while (go && cur_step < steps){
            cur_step ++;

            // Display
            display(display_screen, display_instruction_num, instruction_ptr);


            // Split current instructions into A, B, C, D ptr's
            int[] ptr = get_ptr(instruction);

            // load values at A ptr and B ptr
            int A_val = RAM[ptr[0]];
            int old_A_val = A_val;
            int B_val = RAM[ptr[1]];

            // Do computer instruction
            A_val -= B_val;

            if (A_val < 0){
                instruction_ptr = ptr[2];
                instruction = RAM[instruction_ptr];
            } else {
                instruction_ptr = ptr[3];
                instruction = RAM[instruction_ptr];
            }
            RAM[ptr[0]] = A_val;

            // If the same instruction has been repeated 5 times and value hasn't changed then end program
            if (instruction_ptr == previous_instruction_ptr && A_val == old_A_val){
                repeat_amount ++;

                if (repeat_amount >= 5){
                    go = false;
                }

            } else {
                previous_instruction_ptr = instruction_ptr;
                repeat_amount = 0;
            }


        }

        if (display_tot_instructions){
            System.out.println("Took " + cur_step + " steps to run program.");
        }
        this.end_RAM = RAM;
        this.instruction_ptr = instruction_ptr;
        return RAM;
    }


    private int[] get_ptr(int instruction){
        int[] ptr = new int[4];
        for (int i = 0; i < 4; i ++){
            ptr[i] = instruction & 255;
            instruction >>= 8;
        }
        return ptr;
    }


    private void display(boolean display_screen, boolean display_instruction_num, int instruction_ptr) {
        // Display Screen
        if (display_screen){
            display_screen(RAM);
        }

        // Display current instruction num
        if (display_instruction_num){
            System.out.println(instruction_ptr);
        }
    }


    private void display_screen(int[] RAM) {
        // Create initial variables
        String[] bits = blank_Bits();
        String output = "\n\n";

        // Iterate through screen locations in the RAM
        for (int x = 240; x < 256; x ++){

            // Convert RAM value to binary
            String bin = String.format("%32s", Integer.toBinaryString(RAM[x])).replace(' ','0');

            // Iterate through binary number
            for (int y = 0; y < 32; y++){
                if (bin.charAt(y) == '1'){
                    bits[y] += '⬜';
                } else {
                    bits[y] += '⬛';
                }
            }
        }

        // Iterate through bit rows
        for (int i = 31; i >= 0; i--){
            output += bits[i] + "\n";
        }
        System.out.println(output);
    }


    public String RAM_String(){
        String output = "";
        for (int i = 0; i < this.end_RAM.length; i++){
            output += String.valueOf(this.end_RAM[i]) + "\n";
        }
        return output;
    }
}
