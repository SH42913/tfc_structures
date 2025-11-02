package com.farco.tfc_structures.utils;

import com.farco.tfc_structures.TFCStructuresMod;

public class ClassLoadChecker {
    public final static String TFC_ROOT_CLASS = "net.dries007.tfc.TerraFirmaCraft";
    public final static boolean TFC_IS_LOADED = checkClassIsLoaded(TFC_ROOT_CLASS);

    @SuppressWarnings("SameParameterValue")
    public static boolean checkClassIsLoaded(String className) {
        try {
            var classLoader = TFCStructuresMod.class.getClassLoader();
            Class.forName(className, false, classLoader);
            return true;
        } catch (ClassNotFoundException ignored) {
        }

        return false;
    }
}
