package com.appdynamics.ace.extension.rest.test;

import com.appdynamics.ace.extension.rest.command.GroovyConsoleService;
import com.appdynamics.ace.extension.rest.debug.api.ScriptInfo;
import com.appdynamics.ace.extension.rest.debug.api.ScriptVersionInfo;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;


public class SimpleTest {

    public static final String TEST_SCRIPT_NAME = "testScript";
    public static final String SCRIPT_SRC_1 = "return 3+4";
    public static final String SCRIPT_SRC_2 = "return 9+4";

    @Test
    public void ScriptPush () throws Exception {
        GroovyConsoleService console = new GroovyConsoleService("build/test/tmp");

        console.deleteAllScripts();
        console.uploadGroovy(TEST_SCRIPT_NAME, SCRIPT_SRC_1);


        assertTrue(console.listScripts().contains(TEST_SCRIPT_NAME));
    }

    @Test
    public void ScriptManagement() throws Exception {
        GroovyConsoleService console = new GroovyConsoleService("build/test/tmp");

        console.deleteAllScripts();
        assertEquals(console.listScripts().size(),0);

        console.uploadGroovy(TEST_SCRIPT_NAME, SCRIPT_SRC_1);
        assertTrue(console.listScripts().contains(TEST_SCRIPT_NAME));
        assertEquals(console.listScripts().size(),1);

        ScriptInfo info = console.getScriptInfo(TEST_SCRIPT_NAME);
        assertNotNull(info);
        assertEquals(info.getLastVersion(),1);
        assertEquals(info.getName(), TEST_SCRIPT_NAME);

        assertEquals(console.getScriptSource(TEST_SCRIPT_NAME), SCRIPT_SRC_1);

        console.uploadGroovy(TEST_SCRIPT_NAME, SCRIPT_SRC_2);
        info = console.getScriptInfo(TEST_SCRIPT_NAME);
        assertEquals(info.getLastVersion(), 2);

        assertEquals(console.getScriptSource(TEST_SCRIPT_NAME), SCRIPT_SRC_2);

        console.uploadGroovy(TEST_SCRIPT_NAME, SCRIPT_SRC_2);
        info = console.getScriptInfo(TEST_SCRIPT_NAME);
        assertEquals(info.getLastVersion(),2);

        assertEquals(console.getScriptSource(TEST_SCRIPT_NAME), SCRIPT_SRC_2);
        assertEquals(console.getScriptSource(TEST_SCRIPT_NAME,1), SCRIPT_SRC_1);
    }


    @Test
    public void testVersions() throws Exception {
        GroovyConsoleService console = new GroovyConsoleService("build/test/tmp");

        console.deleteAllScripts();
        assertEquals(console.listScripts().size(),0);

        console.uploadGroovy("script1","1");
        console.uploadGroovy("script1","2");
        console.uploadGroovy("script1","3");
        console.uploadGroovy("script1","4");
        console.uploadGroovy("script1","5");
        console.uploadGroovy("script1","6");

        console.uploadGroovy("script2","1");
        Thread.sleep(2000);
        console.uploadGroovy("script2","2");
        Thread.sleep(2000);
        console.uploadGroovy("script2","3");
        Thread.sleep(2000);


        assertEquals(console.getScriptInfo("script1").getLastVersion(),6);
        assertEquals(console.getScriptInfo("script2").getLastVersion(),3);

        assertEquals(console.getScriptInfo("script1").getHistoryVersions().size(),5);

        // Check Dates
        List<ScriptVersionInfo> v = console.getScriptInfo("script2").getHistoryVersions();
        for (ScriptVersionInfo in : v) {
            System.out.println("  V:"+in.getVersion()+" --Y "+in.getLastModifyDate());
        }

        console.clearScriptHistory();
        assertEquals(console.getScriptInfo("script1").getLastVersion(),1);
    }


}
