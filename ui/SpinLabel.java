package com.ppapp.natalib.ui;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.IntAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class SpinLabel extends Label
{
    //Встроенный экшен
    public static class ValueToAction extends IntAction
    {
        public ValueToAction (){super();}
        public ValueToAction (int start, int end){super(start,end);}

        protected void begin ()
        {
            if(target.getClass() == SpinLabel.class)
            {
                SpinLabel _target = (SpinLabel)target;

                setStart(_target.getValue());
                if(getEnd() < _target.getMin())
                    setEnd(_target.getMin());
                else if(getEnd() > _target.getMax())
                    setEnd(_target.getMax());
            }
        }

        protected void update (float percent)
        {
            super.update(percent);
            if(target!=null)
            {
                if(target.getClass() == SpinLabel.class)
                {
                    if(((SpinLabel)target).getValue() != getValue())
                        ((SpinLabel)target).setValue(getValue());
                }
            }
        }
    }
    //---------------------------------------------------------------------
    //Экшен изменения значения
    static public ValueToAction valueTo(int value, float duration){return valueTo(value, duration, null);}
    static public ValueToAction valueTo(int value){return valueTo(value, 0.0f, null);}
    static public ValueToAction valueTo(int value, float duration, Interpolation interpolation)
    {
        ValueToAction action = Actions.action(ValueToAction.class);
        action.setEnd(value);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }
    //---------------------------------------------------------------------
    //Свойтсва:
    private int value = 0;
    private int min   = 0;
    private int max   = 100;
    private String prefix = "";
    private String postfix = "";
    /** ---------------------- Конструкторы ----------------------------- */
    public SpinLabel(Skin skin){super("",skin); updateShowValue();}
    public SpinLabel(int min, int max, int value, Skin skin)
    {
        super("",skin); this.min = min; this.max = max;
        setValue(value);
    }
    public SpinLabel(int min, int max, int value, String prefix, String postfix, Skin skin)
    {
        super("",skin); this.min = min; this.max = max; this.prefix = prefix; this.postfix = postfix;
        setValue(value);
    }
    public SpinLabel(int min, int max, int value, String prefix, String postfix, Skin skin, String styleName)
    {
        super("",skin,styleName); this.min = min; this.max = max; this.prefix = prefix; this.postfix = postfix;
        setValue(value);
    }
    public SpinLabel(int min, int max, int value, Skin skin, String styleName)
    {
        super("",skin,styleName); this.min = min; this.max = max;
        setValue(value);
    }
    /** ----------------------------------------------------------------- */
    public int getValue(){return value;}
    public void setValue(int value)
    {
        this.value = value;
        if(this.value<min)
            this.value = min;
        else if(this.value>max)
            this.value = max;

        updateShowValue();
    }

    public void updateShowValue()
    {setText(prefix+value+postfix);}

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
        if(value<min)
        {
            value = min;
            updateShowValue();
        }
    }

    @Override
    public float getPrefHeight() {
        return super.getPrefHeight();
    }

    @Override
    public float getPrefWidth() {
        return super.getPrefWidth();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
        if(value>max)
        {
            value = max;
            updateShowValue();
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        updateShowValue();
    }

    public String getPostfix() {
        return postfix;
    }

    public void setPostfix(String postfix) {
        this.postfix = postfix;
        updateShowValue();
    }
}
