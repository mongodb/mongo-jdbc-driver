import yaml

tyName = {
     'EvalNumber': '"numeric"',
     'EvalString': '"string"',
     'EvalUint64': '"long"',
     'EvalInt64': '"long"',
     'EvalInt32': '"int"',
     'EvalPolymorphic': 'null',
     'EvalDatetime': '"date"',
     'EvalDate': '"date"',
     'EvalDouble': '"double"',
     'EvalDecimal128': '"decimal"',
}

def getFunctionInfo(fName):
    y = yaml.load(open(fName), Loader=yaml.FullLoader)
    functions = y['functions']
    functions_info = []
    for fun in functions:
        # There is no good way for the functions_columns tables to handle multiple invocations for each
        # function, so we'll just base off the first invocation, for now.
        invocation = fun['invocations'][0]
        args = []
        for arg in invocation['arguments']:
            args.append(tyName[arg['eval_type']])
        functions_info.append(('"' + fun['_id'] + '"', tyName[invocation['return_type']],
            '"' + fun['description'] + '"', "new String[]{%s}"%(",".join(args))))
    functions_decls = []
    for info in functions_info:
        functions_decls.append('new MongoFunction(%s, %s, %s, %s)'%(info[0].upper(), info[1], info[2], info[3]))
    return functions_decls


print('// This is generated code. To regenerate go to the resources directory:')
print('//     $ cd mongo-jdbc-driver/resources')
print('// and run:')
print('//     $ make')
print('package com.mongodb.jdbc;\n')
print('import java.util.LinkedHashSet;\n')
print('public class MongoFunction {')
print('    public String name;')
print('    public String returnType;')
print('    public String comment;')
print('    public String[] argTypes;\n')
print('    public MongoFunction(String name, String returnType, String comment, String[] argTypes) {')
print('        this.name = name;')
print('        this.returnType = returnType;')
print('        this.comment = comment;')
print('        this.argTypes = argTypes;')
print('    }\n')
print('    public static final MongoFunction[] mySQLFunctions = new MongoFunction[] {')
print(          ',\n        '.join(getFunctionInfo("mysql_scalar_functions.yml")) +',\n        ')
print(          ',\n        '.join(getFunctionInfo("mysql_aggregation_functions.yml")))
print('    };')
print('    public static final String[] mySQLFunctionNames;')
print('    public static final String mySQLNumericFunctionsString;')
print('    public static final String mySQLStringFunctionsString;')
print('    public static final String mySQLDateFunctionsString;')
print('')
print('    static {')
print('        mySQLFunctionNames = new String[MongoFunction.mySQLFunctions.length];')
print('        for (int i = 0; i < mySQLFunctionNames.length; ++i) {')
print('            mySQLFunctionNames[i] = MongoFunction.mySQLFunctions[i].name;')
print('        }')
print('')
print('        LinkedHashSet<String> numericFunctionSet = new LinkedHashSet<>(mySQLFunctionNames.length);')
print('        LinkedHashSet<String> stringFunctionSet = new LinkedHashSet<>(mySQLFunctionNames.length);')
print('        LinkedHashSet<String> dateFunctionSet = new LinkedHashSet<>(mySQLFunctionNames.length);')
print('        for (int i = 0; i < MongoFunction.mySQLFunctions.length; ++i) {')
print('            for (String argType : MongoFunction.mySQLFunctions[i].argTypes) {')
print('                String name = MongoFunction.mySQLFunctions[i].name;')
print('                if (argType == null) {')
print('                    continue;')
print('                }')
print('                switch (argType) {')
print('                    case "string":')
print('                        if (stringFunctionSet.contains(name)) {')
print('                            break;')
print('                        }')
print('                        stringFunctionSet.add(name);')
print('                        break;')
print('                    case "numeric":')
print('                    case "long":')
print('                    case "int":')
print('                    case "double":')
print('                    case "decimal":')
print('                        if (numericFunctionSet.contains(name)) {')
print('                            break;')
print('                        }')
print('                        numericFunctionSet.add(name);')
print('                        break;')
print('                    case "date":')
print('                        if (dateFunctionSet.contains(name)) {')
print('                            break;')
print('                        }')
print('                        dateFunctionSet.add(name);')
print('                        break;')
print('                }')
print('            }')
print('        }')
print('        mySQLNumericFunctionsString = String.join(",", numericFunctionSet);')
print('        mySQLStringFunctionsString = String.join(",", stringFunctionSet);')
print('        mySQLDateFunctionsString = String.join(",", dateFunctionSet);')
print('    }')
print('}')
