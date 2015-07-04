package com.ppapp.natalib;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.XmlReader;
import com.ppapp.natalib.ui.PolyImage;
import com.ppapp.natalib.ui.SpinLabel;

/**
 * Загрузчик слоя UI из XML
 */
public class NataUiLoader
{
    private Skin skin = null;

    //Конструктор
    public NataUiLoader(Skin skin){this.skin = skin;}

    //Загрузка из XML
    public Group LoadUiFromInternalXML(String internalPath)
    {
        Group result = null;
        try
        {
            XmlReader reader = new XmlReader();
            XmlReader.Element root = reader.parse(Gdx.files.internal(internalPath));
            if(root!=null)
            {
                if(root.getName().equalsIgnoreCase("nata_ui"))
                {
                    //Загружаем данные об размерах экрана
                    float rx = 1.0f;
                    float ry = 1.0f;
                    boolean proportions = false;
                    //--------------------------
                    XmlReader.Element resize = root.getChildByName("resize");
                    if(resize!=null)
                    {
                        float w = resize.getFloat("width", (float)Gdx.graphics.getWidth());
                        float h = resize.getFloat("height", (float)Gdx.graphics.getWidth());
                        proportions = resize.getBoolean("proportions",true);
                        rx = (float)Gdx.graphics.getWidth()/w;
                        ry = (float)Gdx.graphics.getHeight()/h;
                    }
                    //--------------------------
                    result = _loadItemsFromXmlElement(root.getChildByName("items"),rx,ry,proportions);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            result = null;
        }
        return result;
    }
    //----------------------------
    //Вспомогательные функции
    private Group _loadItemsFromXmlElement(XmlReader.Element element, float rx, float ry, boolean prop)
    {
        Group result = null;
        if(element!=null)
        {
            result = new Group();
            for (int i = 0; i < element.getChildCount(); i++)
            {
                XmlReader.Element item = element.getChild(i);
                if(item!=null)
                {
                    Actor ui_item = _loadUiElementFromXmlElement(item,rx,ry,prop);

                    if (ui_item != null)
                    {
                        //Загружаем базовые значения X,Y,W,H,OR,SC,NAME
                        ui_item.setName(item.getAttribute("name", "ui_item"));
                        ui_item.setVisible(item.getBooleanAttribute("visible", true));
                        ui_item.setRotation(item.getFloatAttribute("angle", 0.0f));

                        ui_item.setPosition(item.getFloatAttribute("x", 0.0f) * rx,
                                            item.getFloatAttribute("y", 0.0f) * ry);

                        ui_item.setScale(item.getFloatAttribute("scale_x", 1.0f),
                                         item.getFloatAttribute("scale_y", 1.0f));

                        ui_item.setColor(Color.valueOf(item.getAttribute("color", "#FFFFFF").replace("#", "")));

                        //Загружаем информацию о размерах
                        float w_first = item.getFloatAttribute("width",0.0f);
                        float h_first = item.getFloatAttribute("height",0.0f);

                        if(prop) /** ДОДЕЛАТЬ В БУДУЩЕМ */
                        {
                            float w = w_first;
                            float h = h_first;

                            if(w != 0 && h != 0)
                            {
                                h = (w * rx) * (h / w);
                                w = w * rx;
                            }
                            else if(w!=0.0f)
                            {
                                w = w*rx;
                                h = w*(_getActorHeightFromUiClass(ui_item)/ _getActorWidthFromUiClass(ui_item));
                            }
                            else if(h!=0.0f)
                            {
                                h = h*ry;
                                w = h*(_getActorWidthFromUiClass(ui_item)/ _getActorHeightFromUiClass(ui_item));
                            }
                            else
                            {
                                w = _getActorWidthFromUiClass(ui_item)*rx;
                                h = _getActorHeightFromUiClass(ui_item)*rx; //RX
                            }

                            if(h!=0.0f && h_first!=0.0f && rx<ry)
                            {
                                //Поиск смещения по Y
                                if(ui_item.getY()!=0.0f)
                                    ui_item.setY(ui_item.getY()*((ui_item.getY()+h_first * ry - h)/ui_item.getY()));

                            }
                            ui_item.setSize(w,h);
                        }
                        else {

                            if(w_first == 0.0f)
                                w_first = _getActorWidthFromUiClass(ui_item);

                            if(h_first == 0.0f)
                                h_first = _getActorHeightFromUiClass(ui_item);

                            ui_item.setSize(w_first * rx, h_first * ry);
                        }

                        if(item.getAttribute("origin_x","").length()>0 || item.getAttribute("origin_y","").length()>0)
                            ui_item.setOrigin(item.getFloatAttribute("origin_x", ui_item.getWidth() * 0.5f),item.getFloatAttribute("origin_y",ui_item.getHeight()*0.5f));
                        else
                            ui_item.setOrigin(_stringToAlign(item.getAttribute("origin", "center")));

                        result.addActor(ui_item);
                    }
                }
            }
        }
        return result;
    }

    //Загрузка UI элемента
    private Actor _loadUiElementFromXmlElement(XmlReader.Element element, float rx, float ry, boolean prop)
    {
        if(element!=null)
        {
            String name = element.getName().toLowerCase();

            if(name.equalsIgnoreCase("label"))
                return _loadLabelFromXmlElement(element);
            else if(name.equalsIgnoreCase("spinlabel"))
                return _loadSpinLabelFromXmlElement(element);
            else if(name.equalsIgnoreCase("image"))
                return _loadImageFromXmlElement(element);
            else if(name.equalsIgnoreCase("polyimage"))
                return _loadPolyImageFromXmlElement(element);
            else if(name.equalsIgnoreCase("button"))
                return _loadButtonFromXmlElement(element);
            else if(name.equalsIgnoreCase("textbutton"))
                return _loadTextButtonFromXmlElement(element);
            else if(name.equalsIgnoreCase("group"))
                return _loadItemsFromXmlElement(element, rx, ry, prop);
            else if(name.equalsIgnoreCase("slider"))
                return _loadSliderFromXmlElement(element);
            else if(name.equalsIgnoreCase("progressbar"))
                return _loadProgressBarFromXmlElement(element);
            else if(name.equalsIgnoreCase("checkbox"))
                return _loadCheckBoxFromXmlElement(element);
        }
        return null;
    }

    //Загрузка метки
    private Label _loadLabelFromXmlElement(XmlReader.Element element)
    {
        Label result = null;
        if(element!=null)
        {
            String text = element.getAttribute("text", "");
            if (text.length() <= 0)
                text = element.getText();

            NataLocalization localization = NataLocalization.getInstance();
            if(localization!=null && text!=null)
            {
                if (text.trim().indexOf("@key/") == 0)
                    text = localization.getString(text.trim().substring(5));
                else
                    text = localization.tr(text);
            }
            if(element.getAttribute("style_name","").trim().length()>0)
                result = new Label(text,skin,element.getAttribute("style_name", "default"));
            else if(element.getAttribute("font","").trim().length()>0)
                result = new Label(text,skin,element.getAttribute("font","default").trim(),Color.valueOf(element.getAttribute("color", "#FFFFFF").replace("#", "")));
            else
                result = new Label(text,skin);

            result.setAlignment(_stringToAlign(element.getAttribute("text_align","center")));
        }
        return result;
    }

    //Загрузка SpinLabel
    private SpinLabel _loadSpinLabelFromXmlElement(XmlReader.Element element)
    {
        SpinLabel result = null;
        if(element!=null)
        {
            String prefix = element.getAttribute("prefix","");
            String postfix = element.getAttribute("postfix","");

            NataLocalization localization = NataLocalization.getInstance();
            if(localization!=null)
            {
                if(prefix.length()>0) {
                    if (prefix.indexOf("@key/") == 0)
                        prefix = localization.getString(prefix.substring(5));
                    else
                        prefix = localization.tr(prefix);
                }

                if(postfix.length()>0) {
                    if (postfix.indexOf("@key/") == 0)
                        postfix = localization.getString(postfix.substring(5));
                    else
                        postfix = localization.tr(postfix);
                }
            }
            result = new SpinLabel(element.getIntAttribute("min",0),
                                   element.getIntAttribute("max",100),
                                   element.getIntAttribute("value",0),prefix,postfix,skin,
                                   element.getAttribute("style_name","default"));

            result.setAlignment(_stringToAlign(element.getAttribute("text_align","center")));
        }
        return result;
    }

    //Загрузка или создание картики
    private Image _loadImageFromXmlElement(XmlReader.Element element)
    {
        Image result = null;
        if(element!=null)
        {
            String src = element.getAttribute("src","").trim();
            if(src.length()>0) {
                if (src.indexOf("@color/") == 0) {
                    Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
                    pixmap.setColor(Color.valueOf(src.substring(7).replace("#", "")));
                    pixmap.fill();
                    result = new Image(new Texture(pixmap));
                } else {
                    NataLocalization localization = NataLocalization.getInstance();
                    if (localization != null) {
                        if (src.indexOf("@key/") == 0)
                            result = new Image(skin, localization.getString(src.substring(5)));
                        else
                            result = new Image(skin, localization.tr(src));
                    } else
                        result = new Image(skin, src);
                }
            }
        }
        return result;
    }

    //Загрузка множественной картинки
    private PolyImage _loadPolyImageFromXmlElement(XmlReader.Element element)
    {
        PolyImage result = null;
        if(element!=null)
        {
            String src = element.getAttribute("src","").trim();
            NataLocalization localization = NataLocalization.getInstance();
            if (localization != null) {
                if (src.indexOf("@key/") == 0)
                    src = localization.getString(src.substring(5));
                else
                    src = localization.tr(src);
            }
            result = new PolyImage(src, element.getIntAttribute("count",0),
                                        element.getIntAttribute("current",0),skin);
        }
        return result;
    }

    //Загрузка кнопки
    private Button _loadButtonFromXmlElement(XmlReader.Element element)
    {
        Button result = null;
        if(element!=null) {
            result = new Button(skin, element.getAttribute("style_name", "default"));

            result.setChecked(element.getBooleanAttribute("checked", false));
        }

        return result;
    }

    //Загрузка текстовой кнопки
    private TextButton _loadTextButtonFromXmlElement(XmlReader.Element element)
    {
        TextButton result = null;
        if(element!=null)
        {
            String text = element.getAttribute("text","");
            if(text.length()<=0)
                text = element.getText();

            NataLocalization localization = NataLocalization.getInstance();
            if(localization!=null) {
                if (text.trim().indexOf("@key/") == 0)
                    text = localization.getString(text.trim().substring(5));
                else
                    text = localization.tr(text);
            }

            if(element.getAttribute("style_name","").trim().length()>0)
                result = new TextButton(text,skin,element.getAttribute("style_name", "default"));
            else
                result = new TextButton(text,skin);

            result.setChecked(element.getBooleanAttribute("checked",false));
            result.getLabel().setAlignment(_stringToAlign(element.getAttribute("text_align", "center")));
        }
        return result;
    }

    //Загрузка слайдера
    private Slider _loadSliderFromXmlElement(XmlReader.Element element)
    {
        Slider result = null;
        if(element!=null)
        {
            result = new Slider(element.getFloatAttribute("min",0.0f),
                                element.getFloatAttribute("max",1.0f),
                                element.getFloatAttribute("step",0.1f),
                                element.getBooleanAttribute("vertical",false),
                                skin, element.getAttribute("style_name","default"));

            result.setValue(element.getFloatAttribute("value", 0.0f));
        }
        return result;
    }

    //Загрузить прогресбар
    private ProgressBar _loadProgressBarFromXmlElement(XmlReader.Element element)
    {
        ProgressBar result = null;
        if(element!=null)
        {
            result = new ProgressBar(element.getFloatAttribute("min",0.0f),
                    element.getFloatAttribute("max",1.0f),
                    element.getFloatAttribute("step",0.1f),
                    element.getBooleanAttribute("vertical",false),
                    skin, element.getAttribute("style_name","default"));

            result.setValue(element.getFloatAttribute("value", 0.0f));
        }
        return result;
    }

    //Загрузка чекбокса
    private CheckBox _loadCheckBoxFromXmlElement(XmlReader.Element element)
    {
        CheckBox result = null;
        if(element!=null)
        {
            String text = element.getAttribute("text","");
            if(text.length()<=0)
                text = element.getText();

            NataLocalization localization = NataLocalization.getInstance();
            if(localization!=null) {
                if (text.trim().indexOf("@key/") == 0)
                    text = localization.getString(text.trim().substring(5));
                else
                    text = localization.tr(text);
            }

            if(element.getAttribute("style_name","").trim().length()>0)
                result = new CheckBox(text,skin,element.getAttribute("style_name", "default"));
            else
                result = new CheckBox(text,skin);

            result.setChecked(element.getBooleanAttribute("checked", false));
            result.getLabel().setAlignment(_stringToAlign(element.getAttribute("text_align", "center")));
        }
        return result;
    }

    //Преобразуем текстовое значение выравнивания в числовое
    private int _stringToAlign(String align)
    {
        if(align.equalsIgnoreCase("left"))
            return Align.left;
        else if(align.equalsIgnoreCase("right"))
            return Align.right;
        else if(align.equalsIgnoreCase("top"))
            return Align.top;
        else if(align.equalsIgnoreCase("bottom"))
            return Align.bottom;
        else if(align.equalsIgnoreCase("topleft"))
            return Align.topLeft;
        else if(align.equalsIgnoreCase("topright"))
            return Align.topRight;
        else if(align.equalsIgnoreCase("bottomleft"))
            return Align.bottomLeft;
        else if(align.equalsIgnoreCase("bottomright"))
            return Align.bottomRight;
        else
            return Align.center;
    }

    //Получаем размеры UI элементов
    private float _getActorWidthFromUiClass(Actor actor)
    {
        try {
            return ((Layout)actor).getPrefWidth();
        } catch (Exception e) {
            return actor.getWidth();
        }
    }

    private float _getActorHeightFromUiClass(Actor actor)
    {
        try {
            return ((Layout)actor).getPrefHeight();
        } catch (Exception e) {
            return actor.getHeight();
        }
    }
}
