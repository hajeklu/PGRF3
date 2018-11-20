# PGRF3
první úloha

Vytvořte vertex a index buffer pro uložení geometrie tělesa založené na síti trojúhelníků – grid. Vyzkoušejte implementaci gridu pomoci triangle stripu. Model zobrazte ve formě hran i vyplněných ploch
Vytvářená tělesa definujte pomocí parametrických funkcí uvedených například na následujících odkazech. 
http://www.math.uri.edu/~bkaskosz/flashmo/tools/parsur/ 
http://www.math.uri.edu/~bkaskosz/flashmo/tools/sphplot/ 
http://www.math.uri.edu/~bkaskosz/flashmo/tools/cylin/ 
Implementujte alespoň 6 funkcí, dvě v kartézských, dvě ve sférických a dvě v cylindrických souřadnicích. Vždy jedna může být použita z uvedených stránek a druhou „pěknou“ navrhněte. Výpočet geometrie zobrazovaných těles (souřadnic vrcholů) i přepočet na zvolený souřadnicový systém bude prováděn vertexovým programem!
Alespoň jednu z funkcí modifikujte v čase pomocí uniform proměnné.
Vytvořte vhodné pixelové programy pro zobrazení povrchu těles znázorňující barevně pozici (souřadnici xyz, hloubku), barvu, texturu, normálu a souřadnice do textury.
Vytvořte vertexový a pixelový program pro zobrazení texturovaného osvětleného povrchu pomoci Blinn-Phong osvětlovacího modelu, všechny složky. Předpokládejte reflektorový zdroj světla a útlum prostředí. Implementujte hladký přechod na okraji reflektorového světla. Pozici zdroje světla znázorněte.
Pozorovatele ovládejte pomocí kamery myší a WSAD.
Umožněte nastavení ortogonální i perspektivní projekce.
Na vhodných tělesech znázorněte rozdíl mezi výpočtem osvětlení per vertex a per pixel.
Implementujte metodu pro výpočet vržených stínů ShadowMaps. Uvažujte alespoň jeden pohybující se zdroj světla a dvě různá zároveň zobrazená tělesa. Alespoň jedno těleso se musí pohybovat. Pro znázornění vržených stínu vykreslete rovinnou podložku s vypočteným osvětlením.
Před odevzdáním si znovu přečtěte pravidla odevzdávání a hodnocení projektů uvedené v Průvodci studiem
