package com.appdynamics.ace.extension.rest.test;

import com.appdynamics.ace.extension.rest.command.GroovyConsoleService;
import com.appdynamics.ace.extension.rest.debug.api.ScriptResult;
import com.appdynamics.ace.extension.rest.debug.api.ScriptStatus;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: stefan.marx
 * Date: 09.11.13
 * Time: 00:48
 * To change this template use File | Settings | File Templates.
 */
public class GroovyTest {

    @Test
    public void twoStepExecute() throws Exception {

        GroovyConsoleService console = new GroovyConsoleService("build/test/tmp");

        console.deleteAllScripts();

        console.uploadGroovy("script1","return 4*3");
        String result = console.executeScript("script1").getStringResult();
        assertEquals(result, "12");


        System.out.println("RES:"+result);
    }

    @Test
    public void singleStepTest() throws Exception {
        GroovyConsoleService console = new GroovyConsoleService("build/test/tmp");
        console.deleteAllScripts();

        String re = console.executeGroovy("test2", "return 34*44").getStringResult();
        assertEquals(Integer.parseInt(re) , 34*44);
    }


    @Test
    public void singleStepTest2() throws Exception {
        GroovyConsoleService console = new GroovyConsoleService("build/test/tmp");
        console.deleteAllScripts();

        ScriptResult re = console.executeGroovy("test2", "return 34*44");
        assertEquals(re.getClassname(),"java.lang.Integer");
    }

    @Test
    public void multiTest() throws Exception {
        GroovyConsoleService console = new GroovyConsoleService("build/test/tmp");
        console.deleteAllScripts();

        //String.class.getSuperclass()
        console.uploadGroovy("funca","return \"hello\"");
        console.uploadGroovy("funcb","return funca()");
        ScriptResult sr = console.executeScript("funcb");

        assertEquals(sr.getStatus(), ScriptStatus.OK);
        assertEquals(sr.getStringResult(),"hello");


    }
    @Test
    public void multiTestParameter() throws Exception {
        GroovyConsoleService console = new GroovyConsoleService("build/test/tmp");
        console.deleteAllScripts();

        //String.class.getSuperclass()
        console.uploadGroovy("funcx","return args[0]");
        console.uploadGroovy("funca","return \"hello \"+funcx(args[0])");
        console.uploadGroovy("funcb","return funca('stefan')");
        ScriptResult sr = console.executeScript("funcb");

        assertEquals(sr.getStatus(), ScriptStatus.OK);
        assertEquals(sr.getStringResult(),"hello stefan");


    }


}
