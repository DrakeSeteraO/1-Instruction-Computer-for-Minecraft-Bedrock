#Designed by Drake Setera
#Converts Minecraft Bedrock Computer Binary file into a .mcstructure block
from mcstructure import Block, Structure
# from pynbt import BaseTag, NBTFile, TAG_Compound, TAG_Int, TAG_List, TAG_String  # type: ignore

def main(binary_file: str, output_file_name: str):
    RAM = str()
    with open(binary_file, 'r') as file:
        for line in file.readlines():
            RAM += bin((int(line.removesuffix("\n"))+2 ** 32) % 2 ** 32).removeprefix("0b").zfill(32)



    struct = Structure((49, 66, 48), Block("minecraft:structure_void"))
    sx, sy, sz, i = 0, 3, 45, 0
    for x in range(17):
        for z in range(16):
            for y in range(32):
                dx, dy, dz = (3*x + x%2, 2*y+sy, sz-3*z)
                direction = "east"
                if x % 2 == 0:
                    direction = "west"

                struct.set_block((dx, dy, dz+1), Block("minecraft:powered_repeater", {"minecraft:cardinal_direction": "south"}))
                struct.set_block((dx, dy, dz+2), Block("minecraft:daylight_detector_inverted"))


                temp = i
                if temp >= 8192:
                    temp -= 512

                if RAM[temp] == '1':
                    struct.set_block((dx, dy, dz), Block("minecraft:powered_repeater", {"minecraft:cardinal_direction":direction}))
                else:
                    struct.set_block((dx, dy, dz), Block("minecraft:unpowered_repeater", {"minecraft:cardinal_direction": direction}))
                i += 1

    with open(output_file_name, "wb") as f:
        struct.dump(f)



if __name__ == '__main__':
    read_file_name = "computerdor.txt"
    output_file_name = "DisplayFib.mcstructure"

    main(read_file_name, output_file_name)

