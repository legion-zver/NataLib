# NataLib
LibGDX UI Loader from XML

**Версия 0.0.1 (Version 0.0.1)**

Так же содержит в себе механизм работы с локализацией (NataLocalization)

Разработано http://ppapp.ru для автоматизации разработки игр с использованием LibGDX

## Использование:

1. У вас должен быть подготовлен Skin
2. Создаем объект класса NataUiLoader
3. Загружаем XML разметку в Group
```
NataUiLoader loader = new NataUiLoader(skin);
Group ui_layout = loader.LoadUiFromInternalXML("ui/layout.xml");
```
## Локализация
Если вы хотите использовать локализацию загружаемых UI слоев, то необходимо загрузить файл локализации в объект класса NataLocalization
```
if(!NataLocalization.getInstance().LoadFromInternalXML(localizationName,"localization.xml"))
{
    ///Error
}
```

**localizationName** - имя требуемой локализации (ru, en, ...)

**localization.xml (из игры Colorun 2)**
```
<?xml version="1.0" encoding="utf-8"?>
<localization>
    <!-- Строковые значения в игре -->
    <string key="colorun" default="Colorun"><ru>Колоран</ru></string>
    <string key="loading" default="Loading..."><ru>Загрузка...</ru></string>
    <string key="new_star" default="New star"><ru>Новая звезда</ru></string>
    <string key="new_path" default="New path record!"><ru>Новый рекор!</ru></string>
    <string key="speed_level" default="Speed level"><ru>Уровень скорости</ru></string>
    <string key="level" default="Level"><ru>Уровень</ru></string>
    <string key="record" default="Record!"><ru>Рекорд!</ru></string>

    <string key="setup_label" default="Setup"><ru>Настройки</ru></string>
    <!-- Значения для счетчиков -->
    <string key="prefix_have_path" default="You path: "><ru>Пройденный путь: </ru></string>
    <string key="postfix_m" default=" m."><ru> м.</ru></string>
    <string key="prefix_have_points" default="Points: "><ru>Очков: </ru></string>
    <string key="prefix_have_path_mini" default="Path: "><ru>Путь: </ru></string>
    <string key="prefix_have_speed" default="Speed: "><ru>Скорость: </ru></string>
    <!-- Названия ресурсов для разных языков -->
    <string key="img_pause" default="LabelPauseEn"><ru>LabelPauseRu</ru></string>
    <string key="img_levels" default="LabelLevelsEn"><ru>LabelLevelsRu</ru></string>
    <string key="img_endgame" default="LabelGameOverEn"><ru>LabelGameOverRu</ru></string>
</localization>
```
Можно сконфигурировать файл локализации с использованием хранения большого текста:
```
<string key="name_key">
  <default>text text text
  text text text text</default>
  <ru>текст текст текст
  текст текст текст текст</ru>
</string>
```
## Пример XML c UI разметкой
```
<?xml version="1.0" encoding="utf-8"?>
<nata_ui>
    <resize type="screen" width="960.0" height="540.0"/>
    <items>
        <image name="iBackground" src="@color/#FFFFFF" x="0.0" y="0.0" width="960.0" height="540" origin="center"/>
        <image name="iLogo" src="Name" width="420" origin="center"/>
        <image name="iNumber" src="NameNumber" width="100" origin="center"/>
        <label name="lLoading"  x="0.0" y="0.0" height="50.0" width="960.0" text="@key/loading" style_name="default" text_align="center" origin="center"/>
    </items>
</nata_ui>
```
