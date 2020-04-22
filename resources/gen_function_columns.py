import yaml

def tyName(x):
    return {'EvalNumber': '"numeric"',
     'EvalString': '"string"',
     'EvalInt64': '"long"',
     'EvalInt32': '"int"',
     'EvalPolymorphic': 'null',
     'EvalDatetime': '"date"',
     'EvalDate': '"date"',
     'EvalDouble': '"double"',
     'EvalDecimal128': '"decimal"',
    }[x]

y = yaml.load(open('scalar_functions.yml'), Loader=yaml.FullLoader)
functions = y['functions']
functions_info = []
for fun in functions:
    invocation = fun['invocations'][0]
    args = []
    for arg in invocation['arguments']:
        args.append(tyName(arg['eval_type']))
    functions_info.append((fun['_id'], invocation['return_type'], fun['description'], "new String[]{%s}"%(",".join(args))))
functions_decls = []
for info in functions_info:
    functions_decls.append('new MongoSystemFunction("%s", "%s", "%s", %s)'%(info[0].upper(), info[1], info[2], info[3]))


print("public class MongoSystemFunction {")
print("    public String name;")
print("    public String returnType;")
print("    public String comment;")
print("    public String[] argTypes;\n")
print("    public MongoSystemFunction(String name, String returnType, String comment, String[] argTypes) {")
print("        this.name = name;")
print("        this.typeName = typeName;")
print("        this.comment = comment;")
print("        this.returnType = returnType;")
print("        this.argTypes = argTypes;")
print("    }\n")
print("    public static final MongoSystemFunction[] systemFunctions = new MongoSystemFunction[] {")
print("        " + ",\n        ".join(functions_decls))
print("    };")
print("}")
