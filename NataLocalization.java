package com.ppapp.natalib;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Класс отвечающий за загрузку и работу с локализацией (патерн сингелтон)
 */
public class NataLocalization
{
    private static NataLocalization instance = new NataLocalization();
    private HashMap<String, String> strings = new LinkedHashMap<String, String>();
    private HashMap<String, String> translates = new LinkedHashMap<String, String>();
    //---------------------------------------------------------
    private NataLocalization(){}
    public static NataLocalization getInstance() {
        return instance;
    }

    @Override
    protected void finalize() throws Throwable {
        strings.clear();
        translates.clear();
        super.finalize();
    }

    //Загрузка локализации из файла
    public boolean LoadFromInternalXML(String locale, String internalPath)
    {
        strings.clear();
        boolean result = false;
        XmlReader reader = new XmlReader();
        try
        {
            XmlReader.Element root = reader.parse(Gdx.files.internal(internalPath));
            if(root!=null)
            {
                //Проверка на правильность формата файла локализации
                if(root.getName().equalsIgnoreCase("localization"))
                {
                    int load_count = 0;
                    //Перебор всех дочерних элементов
                    for(int i=0;i<root.getChildCount();i++)
                    {
                        XmlReader.Element item = root.getChild(i);
                        if(item!=null)
                        {
                            if(item.getName().equalsIgnoreCase("string"))
                            {
                                String key = item.getAttribute("key").trim();
                                String default_string;
                                String value;
                                if(key.length()>0)
                                {
                                    Array<XmlReader.Element> find = item.getChildrenByName("default");
                                    if(find.size>0)
                                        default_string = find.get(0).getText();
                                    else
                                        default_string = item.getAttribute("default","");

                                    find = item.getChildrenByName(locale);
                                    if(find.size>0)
                                        value = find.get(0).getText();
                                    else
                                        value = default_string;

                                    find.clear();
                                    if(value.length()>0)
                                    {
                                        strings.put(key, value);

                                        if(default_string.length()>0)
                                            translates.put(default_string,value);

                                        load_count++;
                                    }
                                    else
                                    {
                                        if(default_string.length()>0) {
                                            strings.put(key, default_string);
                                            load_count++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(load_count>0)
                        result = true;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            strings.clear();
            result = false;
        }
        return result;
    }

    //Перевод текста от стандартного к локализованному
    public String tr(String value)
    {
        if(translates.containsKey(value))
            return translates.get(value);
        else
            return value;
    }

    //Получаем значение текста по ключу
    public String getString(String key)
    {
        if(strings.containsKey(key))
            return strings.get(key);
        else
            return key;
    }
}
