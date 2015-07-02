import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Marko Bastovanovic (markob@vast.com)
 */

public class ParseStoredHtmlFiles {
    public static void main (String [] args){
        try{
            Multiset<String> points = HashMultiset.create();

            //noinspection ConstantConditions
            for (File file: new File(CrawlCharts.STORE_LOC).listFiles()) {
                Integer distance = null;
                boolean pescana = false;
                //System.out.println("Processing " + URLDecoder.decode(file.getName(), "UTF-8"));
                Document document = Jsoup.parse(FileUtils.readFileToString(file));
                Pattern distanceP = Pattern.compile("<li>Plaža: <span>([0-9]+) m</span></li>");
                Pattern pescanaP = Pattern.compile("Vrsta plaže.*Pješčana", Pattern.DOTALL);
                for (Element element : document.body().getElementById("dist").getElementsByClass("pdet_minfo")) {

                    Matcher pescanaM = pescanaP.matcher(element.toString());
                    if (pescanaM.find()){
                        pescana = true;
                    }

                    //System.out.println("poc" + element + "kraj");
                    for (Element subElement : element.getAllElements()) {
                        if (subElement.className().equals("pdet_minfo_body left")){
                            Matcher m = distanceP.matcher(subElement.toString());
                            if (m.find()){
                                distance = Integer.parseInt(m.group(1));
                            }
                        }
                    }
                }
                if (distance != null && distance<=200 && pescana){
                    System.out.println(URLDecoder.decode(file.getName(), "UTF-8"));
                }
            }


        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
