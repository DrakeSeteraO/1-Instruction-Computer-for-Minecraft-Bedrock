# Designed by BratWORST
bits = 32
bits = bits-(bits%4)
bites = pow(2,bits//4)
ins = 0
vins = bites-1
pins = 0
pgap = vins-ins+1
names = {}
stack = list()
stack2 = list()
stack3 = list()
returns = {}
mem = [0 for _ in range(256)]
def con(arr):
    num=arr[3]
    num*=bites
    num+=arr[2]
    num*=bites
    num+=arr[1]
    num*=bites
    num+=arr[0]
    return(num)
def do(arr):
    global ins
    global mem
    if len(arr) == 4:
        mem[ins] = con(arr)
    elif len(arr) == 3:
        mem[ins] = con([arr[0],arr[1],arr[2],arr[2]])
    elif len(arr) == 2:
        mem[ins] = con([arr[0],arr[1],ins+1,ins+1])
    elif len(arr) == 1:
        mem[ins] = con([arr[0],arr[0],ins+1,ins+1])
    ins += 1
def status(func):
    global pins
    global pgap
    print(func+", "+str(pins)+", "+str(pgap-(vins-ins+1))+" lines used")
    pins = ins
    pgap = vins-ins+1
with open('codedor.txt', 'r') as file:
    lines = file.readlines()
    for line in lines:
        words = line.split()
        if len(words)<=0:
            continue
        
        if words[0] == "var":#1
            names.update({words[1]:vins})
            mem[vins] = int(words[2])                
            vins-=1
        
        if words[0] == "arr":
            if(words[1] == "def" and len(words) == 4):#x
                vins-=int(words[3])
                vins+=1
                names.update({words[2]:vins})
                vins-=1
            elif(words[1] == "def" and len(words) == 5):#0
                mem[names.get(words[2])+int(words[3])] = words[4]
            elif(words[1] == "get"):#15
                do([names.get("zero"),names.get(words[3])])
                do([ins+10,ins+10,ins+2])
                mem[ins] = -con([names.get("zero"),names.get(words[2]),ins+10,ins+10])
                ins+=1
                do([ins+8,ins-1])
                do([names.get("temp"),names.get("byte"),ins+1,ins+5])
                do([names.get("temp2"),names.get("zero")])
                do([names.get("zero"),names.get("temp2")])
                do([names.get("temp2")])
                do([names.get("temp"),names.get("none"),ins-3,ins+1])
                do([ins+2,names.get("zero")])
                do([names.get("zero")])
                ins+=1
                do([names.get(words[4])])
                do([names.get(words[4]),names.get("zero")])
                do([names.get("zero")])
            elif(words[1] == "set"):#20
                do([names.get("zero"),names.get(words[3])])
                do([ins+16])
                do([ins+16])
                do([ins+14,names.get("zero")])
                do([ins+14,names.get("zero"),ins+3])
                mem[ins] = -con([names.get(words[2]),names.get(words[2]),ins+13,ins+13])
                ins+=1
                mem[ins] = -con([names.get(words[2]),names.get("zero"),ins+13,ins+13])
                ins+=1
                do([ins+10,ins-2])
                do([ins+10,ins-2])
                do([names.get("temp"),names.get("byte"),ins+1,ins+5])
                do([names.get("temp2"),names.get("zero")])
                do([names.get("zero"),names.get("temp2")])
                do([names.get("temp2")])
                do([names.get("temp"),names.get("none"),ins-3,ins+1])
                do([ins+3,names.get("zero")])
                do([names.get("zero")])
                do([names.get("zero"),names.get(words[4])])
                ins+=1
                ins+=1
                do([names.get("zero")])

        elif words[0] == "sub":
            if(len(words)==3):#1
                do([names.get(words[1]),names.get(words[2])])
            elif(len(words)==4):#5
                do([names.get(words[3]),names.get(words[3])])
                do([names.get("zero"),names.get(words[1])])
                do([names.get(words[3]),names.get("zero")])
                do([names.get(words[3]),names.get(words[2])])
                do([names.get("zero")])
                
        elif words[0] == "neg":
            if(len(words)==2):#6
                do([names.get("zero"),names.get(words[1])])
                do([names.get("temp"),names.get("zero")])
                do([names.get(words[1]),names.get("temp")])
                do([names.get(words[1]),names.get("temp")])
                do([names.get("zero")])
                do([names.get("temp")])
            elif(len(words)==3):#2
                do([names.get(words[2])])
                do([names.get(words[2],names.get(words[1]))])

        elif words[0] == "not":
            if(len(words)==2):#7
                do([names.get("zero"),names.get(words[1])])
                do([names.get("temp"),names.get("zero")])
                do([names.get(words[1]),names.get("temp")])
                do([names.get(words[1]),names.get("temp")])
                do([names.get("zero")])
                do([names.get("temp")])
                do([names.get(words[1]),names.get("one")])
            elif(len(words)==3):#3
                do([names.get(words[2])])
                do([names.get(words[2],names.get(words[1]))])
                do([names.get(words[2]),names.get("one")])
            
        elif words[0] == "add":
            if(len(words)==3):#3
                do([names.get("zero"),names.get(words[2])])
                do([names.get(words[1]),names.get("zero")])
                do([names.get("zero")])
            elif(len(words)==4):#5
                do([names.get("zero"),names.get(words[1])])
                do([names.get("zero"),names.get(words[2])])
                do([names.get(words[3])])
                do([names.get(words[3]),names.get("zero")])
                do([names.get("zero")])
                
        elif words[0] == "stop":#1
            do([names.get("zero"),names.get("zero"),ins])
        
        elif words[0] == "set":#4
            do([names.get("zero"),names.get(words[2])])
            do([names.get(words[1])])
            do([names.get(words[1]),names.get("zero")])
            do([names.get("zero")])
        
        elif words[0] == "mul":#11
            if(len(words)==3):
                do([names.get("temp"), names.get(words[2]),ins+1,ins+3])
                do([names.get("temp2"),names.get(words[1])])
                do([names.get("temp"),names.get("none"),ins-1, ins+6])
                do([names.get("zero"),names.get(words[1]),ins+2])
                do([names.get("temp2"),names.get("zero")])
                do([names.get("temp"), names.get("one"),ins+1,ins-1])
                do([names.get("temp")])
                do([names.get("zero")])
                do([names.get(words[1])])
                do([names.get(words[1]),names.get("temp2")])
                do([names.get("temp2")])
            elif(len(words)==4):
                do([names.get("temp"), names.get(words[2]),ins+1,ins+3])
                do([names.get("temp2"),names.get(words[1])])
                do([names.get("temp"),names.get("none"),ins-1, ins+6])
                do([names.get("zero"),names.get(words[1]),ins+2])
                do([names.get("temp2"),names.get("zero")])
                do([names.get("temp"), names.get("one"),ins+1,ins-1])
                do([names.get("temp")])
                do([names.get("zero")])
                do([names.get(words[3])])
                do([names.get(words[3]),names.get("temp2"),ins+1])
                do([names.get("temp2")])
            
        elif words[0] == "abs":#7
            do([names.get(words[1]),names.get("zero"),ins+1,ins+7])
            do([names.get("zero"),names.get(words[1])])
            do([names.get(words[1])])
            do([names.get("temp"),names.get("zero")])
            do([names.get(words[1]),names.get("temp")])
            do([names.get("temp")])
            do([names.get("zero")])

        elif words[0] == "div":#wip
            if(len(words)==3):#5
                do([names.get(words[1]),names.get(words[2]),ins+2,ins+1])
                do([names.get("zero"),names.get("one"),ins-1,ins+1])
                do([names.get(words[1])])
                do([names.get(words[1]),names.get("zero")])
                do([names.get("zero")])
            elif(len(words)==4):#9
                do([names.get(words[3])])
                do([names.get("zero"),names.get(words[1])])
                do([names.get(words[3]),names.get("zero")])
                do([names.get("zero")])
                do([names.get(words[3]),names.get(words[2]),ins+2,ins+1])
                do([names.get("temp"),names.get("one"),ins-1,ins+1])
                do([names.get(words[3])])
                do([names.get(words[3]),names.get("temp")])
                do([names.get("temp")])
            
        elif words[0] == "def":#1+x
            ins+=1
            names.update({words[1]:ins})
            stack.append(ins)
            stack2.append("def")
            for i in range(len(words)-2):
                names.update({words[len(words)-i-1]:vins})
                vins-=1
            names.update({words[1]+" return":vins})
            stack3.append(words[1])
            asshole = list()
            for i in range(len(words)-2):
                asshole.append(words[i+2])
            returns.update({words[1]:asshole})

        elif words[0] == "run":#3+4x+6y
            funstar = ins
            names.update({words[0]+str(funstar):vins})
            vins-=1
            for i in range(len(words)-2):
                do([names.get(words[1]+" return")+i+1])
                do([names.get("zero"),names.get(words[i+2])])
                do([names.get(words[1]+" return")+i+1,names.get("zero")])
                do([names.get("zero")])
                if returns.get(words[1])[i] in returns:
                    mem[vins] = -con([names.get(words[i+2]),names.get(words[i+2]),returns.get(returns.get(words[1])[i]),returns.get(returns.get(words[1])[i])])
                    vins-=1
                    mem[vins] = -con([names.get(words[i+2]),names.get("zero"),returns.get(returns.get(words[1])[i])+1,returns.get(returns.get(words[1])[i])+1])
                    vins-=1
                    do([returns.get(returns.get(words[1])[i])-1])
                    do([returns.get(returns.get(words[1])[i])])
                    do([returns.get(returns.get(words[1])[i])-1,vins+2])
                    do([returns.get(returns.get(words[1])[i]),vins+1])
            do([names.get(words[1]+" end")])
            do([names.get(words[1]+" end"),names.get(words[0]+str(funstar)),names.get(words[1])]) 
            mem[names.get(words[0]+str(funstar))] = -con([names.get("none"),names.get("zero"),ins,ins])

        elif words[0] == "end":
            if(stack2[-1] == "def"):#1+4y
                for i in range(len(words)-1):
                    do([names.get("zero"),names.get(stack3[-1]+" return")+returns.get(stack3[-1]).index(words[i+1])+1])
                    returns.update({words[i+1]:ins+1})
                    ins+=1
                    ins+=1
                    do([names.get("zero")])
                names.update({stack3[-1]+" end":ins})
                ins+=1
                stack3.pop()
                mem[stack[-1]-1] = con([names.get("zero"), names.get("zero"), ins,ins])
            
            elif(stack2[-1]=="if"):#0
                mem[stack[-1]]+=ins*bites*bites*bites
            stack.pop()
            stack2.pop()
        
        elif(words[0] == "if"):
            if(words[2] == "!="):#7
                do([names.get("zero"),names.get(words[3])])
                do([names.get("temp"),names.get("zero")])
                do([names.get("zero")])
                do([names.get("temp"),names.get(words[1]),ins+3,ins+1])
                stack.append(ins)
                do([names.get("zero"),names.get("temp"), ins+1,0])
                do([names.get("zero")])
                do([names.get("temp")])
            elif(words[2] == "=" or words[2] == "=="):
                do([names.get("zero"),names.get(words[3])])
                do([names.get("temp"),names.get("zero")])
                do([names.get("zero")])
                do([names.get("temp"),names.get(words[1]),ins+3,ins+1])
                do([names.get("zero"),names.get("temp"), ins+1,ins+3])
                do([names.get("zero")])
                stack.append(ins)
                do([names.get("temp"), names.get("temp"),0])
            ifa = 0#6
            ifb = 0
            if(words[2] == "<"):
                ifa = 1
                ifb = 2
            elif(words[2] == ">"):
                ifa = 3
                ifb = 2
            elif(words[2] == "<=" or words[2] == "=<"):
                ifa = 3
                ifb = 1
            elif(words[2] == ">=" or words[2] == "=>"):
                ifa = 1
                ifb = 1
            if(ifa>0):
                do([names.get("zero"),names.get(words[ifa])])
                do([names.get("temp"),names.get("zero")])
                do([names.get("zero")])
                do([names.get("temp"),names.get(words[4-ifa]),ins+ifb,ins+(3-ifb)])
                stack.append(ins)
                do([names.get("temp"), names.get("temp"),0])
                do([names.get("temp")])
            stack2.append("if")
        
        elif(words[0] == "else"):#1
            mem[stack[-1]]+=(ins+1)*bites*bites
            stack[-1] = ins
            do([names.get("zero"), names.get("zero"), 0])
        
        elif(words[0] == "cp"):#0
            names.update({words[1]:ins})
            
        elif(words[0] == "goto"):#1
            do([names.get("zero"),names.get("zero"), names.get(words[1])])
            
        elif(words[0] == "shift"):
            if(len(words) ==2):#3
                do([names.get("zero"),names.get(words[1])])
                do([names.get(words[1]),names.get("zero")])
                do([names.get("zero")])
            elif(len(words)==3):#5
                do([names.get("temp"),names.get(words[2]),ins+1,ins+5])
                do([names.get("zero"),names.get(words[1])])
                do([names.get(words[1]),names.get("zero")])
                do([names.get("zero")])
                do([names.get("temp"),names.get("none"),ins-3,ins+1])
        
        elif(words[0] == "zero"):#1
            do([names.get(words[1])])
        
        elif(words[0] == "shiftr"):#wip
            if len(words) == 3:#20
                do([names.get("temp"), names.get("bits")])
                do([names.get("zero"),names.get(words[2])])
                do([names.get("temp"), names.get("zero")])
                do([names.get("zero")])
                do([names.get("zero"),names.get(words[1])])
                do([names.get("temp2"),names.get("zero")])
                do([names.get("zero")])
                do([names.get("temp"),names.get("zero"),ins+1,ins+12])
                do([names.get(words[1])])
                do([names.get("zero"), names.get(words[1])])
                do([names.get(words[1]), names.get("zero")])
                do([names.get("zero")])
                do([names.get("temp2"),names.get("zero"),ins+2,ins+1])
                do([names.get("none"),names.get("zero"),ins+2,ins+1])
                do([names.get(words[1]),names.get("none")])
                do([names.get("zero"),names.get("temp2")])
                do([names.get("temp2"),names.get("zero")])
                do([names.get("zero")])
                do([names.get("temp"),names.get("none"),ins-9,ins+1])
                do([names.get("temp2")])
            else:#15
                mem[ins] = con([names.get("temp"), names.get("thirtyone")])
                mem[ins] = con([names.get("zero"),names.get(words[1])])
                mem[ins] = con([names.get("temp2"),names.get("zero")])
                mem[ins] = con([names.get("zero")])
                mem[ins] = con([names.get(words[1])])
                mem[ins] = con([names.get("zero"), names.get(words[1])])
                mem[ins] = con([names.get(words[1]), names.get("zero")])
                mem[ins] = con([names.get("zero")])
                mem[ins] = con([names.get("temp2"),names.get("zero"),ins+1,ins+2])
                mem[ins] = con([names.get(words[1]),names.get("none")])
                mem[ins] = con([names.get("zero"),names.get("temp2")])
                mem[ins] = con([names.get("temp2"),names.get("zero")])
                mem[ins] = con([names.get("zero")])
                mem[ins] = con([names.get("temp"),names.get("none"),ins-9,ins+1])
                mem[ins] = con([names.get("temp2")])

        status(words[0])
print(stack)
        
print(str(bites-pgap+1)+"/"+str(bites)+" used, "+str(100.0*(bites-pgap+1)/bites)+"% full")
with open('computerdor.txt', 'w') as fil:
    for i in range(bites):
        if (mem[i]>=pow(2,bits-1)):
            fil.write(str(mem[i]-pow(2,bits)))
        else:
            fil.write(str(mem[i]))
        fil.write("\n")
            