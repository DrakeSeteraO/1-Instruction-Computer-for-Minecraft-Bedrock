// MinecraftComputer.RAM_to_ptr made by Drake Setera
package MinecraftComputer;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;


public class RAM_to_ptr {
    public int[] RAM = new int[256];

    public RAM_to_ptr(String RAM_file){
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


    public void convert_to_ptr_file(String output_file_name){
        String output = "num | A   B   C   D   | value\n";
        for (int i = 0; i < this.RAM.length; i++){
            String temp1 = String.valueOf(i);
            output += temp1;

            int it = 4 - temp1.length();
            for (int x = 0; x < it; x++)
                output += " ";
            output += "| ";

            int[] ptr = get_ptr(this.RAM[i]);
            for (int y = 0; y < 4; y++){
                String temp = String.valueOf(ptr[y]);
                output += temp;

                int iter = 4 - temp.length();
                for (int z = 0; z < iter; z++)
                    output += " ";
            }

            output += "| ";
            output += String.valueOf(this.RAM[i]) + "\n";
        }

        try {
            // Write output to file
            FileWriter file = new FileWriter(output_file_name);
            file.write(output);
            file.close();

        } catch (Exception ex){
            System.out.println("There was an error saving the file");
        }
    }


    public int[] get_ptr(int num){
        byte[] ptr = {(byte)((num >> 24) & 0xff), (byte)((num >> 16) & 0xff), (byte)((num >> 8) & 0xff), (byte)((num >> 0) & 0xff)};
        int[] output = {0,0,0,0};
        for (int i = 0; i < 4; i++){
            output[i] = (ptr[3-i] + 256) % 256;
        }
        return output;
    }
}
