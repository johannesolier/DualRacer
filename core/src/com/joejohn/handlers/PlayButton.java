package com.joejohn.handlers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PlayButton extends GameButton {


    public PlayButton(TextureRegion tex, float x, float y, OrthographicCamera cam) {
        super(tex, x, y, cam);
    }



    public void update(float dt) {
        if(!enabled) return;

        vec.set(Controls.x, Controls.y, 0);
        cam.unproject(vec);

        if(Controls.isPressed() &&
                vec.x > x - width / 2 && vec.x < x + width / 2 &&
                vec.y > y - height / 2 && vec.y < y + height / 2) {
            clicked = true;
        }
        else {
            clicked = false;
        }
    }
}
