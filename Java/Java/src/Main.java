import MinecraftComputer.Compiler;
import MinecraftComputer.Emulator;
import MinecraftComputer.RAM_to_ptr;

public class Main {
    public static void main(String[] args) {
        // Example code to compile the file test2.asm
        Compiler compile = new Compiler("test2.asm");
        compile.compile("test2.bin");


        // Example code to convert the test2.bin binary file into its parts
        // Useful for debugging or understanding how code compiled
        RAM_to_ptr ptr = new RAM_to_ptr("test2.bin");
        ptr.convert_to_ptr_file("test2.ptr");


        // Example code to run Emulator with the binary file test2.bin
        Emulator comp = new Emulator("test2.bin");
        comp.run();
    }
}