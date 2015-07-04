package com.ppapp.natalib.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.IntAction;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.LinkedList;

/**
 * Класс для отображения покадровой анимации и других типичных задач
 * (возмжно переделать в будущем от Image scene2d)
 */
public class PolyImage extends Actor
{
    public static class ImageToAction extends IntAction
    {
        public ImageToAction (){super();}
        public ImageToAction (int start, int end){super(start,end);}
        protected void begin ()
        {
            if(target.getClass() == PolyImage.class)
            {
                PolyImage _target = (PolyImage)target;
                setStart(_target.getCurrentIndex());
                if(getEnd()>0)
                {
                    if(getEnd() >= _target.getCountImages())
                        setEnd(_target.getCountImages()-1);
                }
                else
                    setEnd(0);
            }
        }

        protected void update (float percent) {
            super.update(percent);
            if(target!=null)
            {
                if(target.getClass() == PolyImage.class)
                {
                    if(((PolyImage)target).getCurrentIndex() != getValue())
                        ((PolyImage)target).setCurrentImage(getValue());
                }
            }
        }
    }
    //---------------------------------------------------------------
    static public ImageToAction imageToAction(int indexImageTo, float duration){return imageToAction(indexImageTo, duration, null);}
    static public ImageToAction imageToAction(int indexImageTo){return imageToAction(indexImageTo, 0.0f, null);}
    static public ImageToAction imageToAction(int indexImageTo, float duration, Interpolation interpolation)
    {
        ImageToAction action = Actions.action(ImageToAction.class);
        action.setEnd(indexImageTo);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }
    //---------------------------------------------------------------
    private TextureAtlas.AtlasRegion             current_image = null;
    private LinkedList<TextureAtlas.AtlasRegion> images = null;
    //---------------------------------------------------------------
    public PolyImage(LinkedList<TextureAtlas.AtlasRegion> images, int currentIndex)
    {
        super();
        this.images = images;
        setCurrentImage(currentIndex);
    }

    public PolyImage(String imageGroupName, int countImages, int currentIndexImage, Skin skin)
    {
        super();
        if(skin!=null)
        {
            images = new LinkedList<TextureAtlas.AtlasRegion>();
            for(int i=0;i<countImages;i++)
            {
                TextureRegion region = skin.getRegion(imageGroupName+i);
                if(region!=null)
                    images.add((TextureAtlas.AtlasRegion) region);
            }
            setCurrentImage(currentIndexImage);
        }
    }
    //---------------------------------------------------------------
    public void setFirstImage()
    {
        if(images!=null) {
            current_image = images.get(0);
        }
    }

    public void setLastImage()
    {
        if(images!=null) {
            current_image = images.get(images.size()-1);
        }
    }

    public TextureAtlas.AtlasRegion getCurrentImage(){return current_image;}

    public int getCountImages()
    {
        if(images!=null)
            return images.size();

        return 0;
    }

    public int getCurrentIndex()
    {
        if(images!=null && current_image!=null)
            return images.indexOf(current_image);

        return 0;
    }

    public void setCurrentImage(int index)
    {
        if(images!=null) {
            if (index >= 0 && index < images.size()) {
                current_image = images.get(index);
            }
        }
    }

    public float getPrefWidth()
    {
        if(current_image!=null)
            return current_image.getRegionWidth();
        else {
            if(images!=null)
            {
                if(images.size()>0)
                    return images.getFirst().getRegionWidth();
            }
            return 0;
        }
    }

    public float getPrefHeight()
    {
        if(current_image!=null)
            return current_image.getRegionHeight();
        else {
            if(images!=null)
            {
                if(images.size()>0)
                    return images.getFirst().getRegionHeight();
            }
            return 0;
        }
    }

    @Override
    public float getWidth() //Переопределили для поддержки корректой загрузки в NataUiLoader
    {
        if(super.getWidth()!=0.0f)
            return super.getWidth();
        else
            return getPrefWidth();
    }

    @Override
    public float getHeight() //Переопределили для поддержки корректой загрузки в NataUiLoader
    {
        if(super.getHeight()!=0.0f)
            return super.getHeight();
        else
            return getPrefHeight();
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        if(batch!=null && current_image!=null && isVisible() && parentAlpha>0.0f)
        {
            batch.setColor(getColor().r,getColor().g,getColor().b,getColor().a*parentAlpha);
            batch.draw(current_image,getX(),getY(),getOriginX(),getOriginY(),getWidth(),getHeight(),getScaleX(),getScaleY(),getRotation());
        }
    }
}
