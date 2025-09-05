// MinecraftComputer.Required_vars made by Drake Setera
package MinecraftComputer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Required_vars {
    private HashMap<String, func_vars> all_func_vars = new HashMap<>();

    public Required_vars(){
        all_func_vars.put("neg", new func_vars(List.of("temp"), List.of("0")));
        all_func_vars.put("not", new func_vars(List.of("temp", "1"), List.of("0", "1")));
        all_func_vars.put("abs", new func_vars(List.of("temp"), List.of("0")));
        all_func_vars.put("mult", new func_vars(List.of("temp", "temp2", "1", "-1"), List.of("0", "0", "1", "-1")));
        all_func_vars.put("mult+", new func_vars(List.of("1", "-1"), List.of("1", "-1")));
        all_func_vars.put("shiftl", new func_vars(List.of("temp", "-1"), List.of("0", "-1")));
        all_func_vars.put("shiftr", new func_vars(List.of("temp", "temp2", "temp3", "32", "-1"), List.of("0", "0", "0", "32", "-1")));
        all_func_vars.put("wait", new func_vars(List.of("-1"), List.of("-1")));
        all_func_vars.put("pow+", new func_vars(List.of("temp", "temp2", "-1"), List.of("0", "0", "-1")));
        all_func_vars.put("create_array", new func_vars(List.of("arr.temp"), List.of("0")));
        all_func_vars.put("arr.get", new func_vars(List.of("arr.a", "arr.b", "arr.c", "arr.temp", "arr.inv_ptr", "temp", "temp2", "temp3", "8", "-1"), List.of("0", "0", "0", "0", "0", "0", "0", "0", "8", "-1")));
        all_func_vars.put("arr.set", new func_vars(List.of("arr.a", "arr.b", "arr.c", "arr.temp", "arr.inv_ptr", "temp", "temp2", "temp3", "1", "8", "-1"), List.of("0", "0", "0", "0", "0", "0", "0", "0", "1", "8", "-1")));

    }

    public boolean test_if_requires_variables(String func_name){
        return all_func_vars.containsKey(func_name);
    }

    public ArrayList<String> get_var_names(String func_name){
        return all_func_vars.get(func_name).names;
    }

    public ArrayList<String> get_var_values(String func_name){
        return all_func_vars.get(func_name).values;
    }
}


class func_vars {
    public ArrayList<String> names = new ArrayList<>();
    public ArrayList<String> values = new ArrayList<>();

    public func_vars(List<String> names, List<String> values){
        for (int i = 0; i < names.size(); i ++){
            this.names.add(names.get(i));
            this.values.add(values.get(i));
        }
    }
}