package org.oreo.rallycap.pleasedontlook;

import phonon.nodes.war.FlagWar;

public class GetWarClass {

    //Yes this class is here JUST to get war status from nodes
    // I have tried to do this in kotlin, but I swear to god there is no way I know of
    // The boolean in Nodes is marked as "internal" so it shouldn't be accessible at all but Java just somehow does it

    public static boolean isWarOn(){
        return FlagWar.INSTANCE.getEnabled$nodes();
    }
}
