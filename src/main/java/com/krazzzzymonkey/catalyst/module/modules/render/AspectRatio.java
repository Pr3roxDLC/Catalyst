package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import org.lwjgl.util.glu.Project;

//TODO Bring back the old logic for hand/world switch
public class AspectRatio extends Modules {

//    public static ModeValue mode = new ModeValue("Mode", new Mode("ViewModel", false), new Mode("Render", false), new Mode("Both", true));
//    public static IntegerValue angle = new IntegerValue("CustomFOV", 90, 10, 180, "Changes your FOV");
    public static DoubleValue ratio = new DoubleValue("Ratio", 1, 0.1, 3, "");


    public static AspectRatio INSTANCE = null;



    public AspectRatio() {
        super("AspectRatio", ModuleCategory.RENDER, "Changes the aspect ratio of your game");
        INSTANCE = this;
      //  addValue(mode, angle, ratio);
        addValue(ratio);
    }


    public static void project(float oldFovY, float oldAspectRatio, float oldZNear, float oldZFar, boolean fromHands) {
        if(INSTANCE.isToggled()){
            Project.gluPerspective(oldFovY, ratio.getValue().floatValue(), oldZNear, oldZFar);
        }else{
            Project.gluPerspective(oldFovY, oldAspectRatio, oldZNear, oldZFar);
        }
//        if (INSTANCE.isToggled() && (fromHands || mode.getMode("Render").isToggled() || mode.getMode("Both").isToggled())) {
//            Project.gluPerspective(angle.getValue(), (mode.getMode("ViewModel").isToggled() || mode.getMode("Both").isToggled()) ?  ratio.getValue().floatValue() : oldAspectRatio, oldZNear, oldZFar);
//        } else {
//            Project.gluPerspective(oldFovY, oldAspectRatio, oldZNear, oldZFar);
//        }
    }
}
