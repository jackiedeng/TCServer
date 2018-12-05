package com.springapp.mvc;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * Created by jackiedeng on 2018/11/10.
 */
public class BaseDispatchController {

//    protected HashMap<String,Invoker> methods;

    public BaseDispatchController(){

        initialize();
    }

    public synchronized void initialize() {
        try {
//            if (methods != null) return;

//            methods = new HashMap<String, Invoker>();
            for (Method m : getClass().getDeclaredMethods()) {
                try {

                    if (Modifier.isStatic(m.getModifiers())) continue;
                    if (!Modifier.isPublic(m.getModifiers())) continue;

                    System.out.print(m);
//                    if (m.getAnnotation(DispatchIgnore.class) != null) continue;
//                    if (!methods.containsKey(m.getName())) {
//                        methods.put(m.getName(), new MethodInvoker(dataResolverService, getClass(), m));
//                    }
                }catch (Throwable e){
//                    methods.put(m.getName(), new PredefinedInvoker(ExceptionUtils.getStackFrames(e)));
                }
            }
        } catch (Throwable e) {

            e.printStackTrace();

        }
    }

    public static void main(String[]args){

        BaseDispatchController baseDispatchController = new BaseDispatchController();

    }
}
