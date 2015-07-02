import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Marko Bastovanovic (markob@vast.com)
 */

public class CrawlCharts {
    public static final String [] LOCATION_HOME = new String[] {
            "http://www.apartmanija.hr/pretraga/apartmani/istra++osobe:3+2.",
            "http://www.apartmanija.hr/pretraga/apartmani/kvarner-i-gorje++osobe:3+3.",
            "http://www.apartmanija.hr/pretraga/apartmani/otoci+krk+osobe:3+11.117",
            "http://www.apartmanija.hr/apartmani/malinska",
            "http://www.apartmanija.hr/apartmani/silo",
            "http://www.apartmanija.hr/apartmani/pinezic",
            "http://www.apartmanija.hr/apartmani/punat",
            "http://www.apartmanija.hr/apartmani/krk-cizici",
            "http://www.apartmanija.hr/apartmani/porat",
            "http://www.apartmanija.hr/apartmani/baska",
            "http://www.apartmanija.hr/apartmani/glavotok",
            "http://www.apartmanija.hr/apartmani/dobrinj",
            "http://www.apartmanija.hr/apartmani/njivice",
            "http://www.apartmanija.hr/apartmani/klimno",
            "http://www.apartmanija.hr/apartmani/jurandvor",
            "http://www.apartmanija.hr/apartmani/vrbnik",
            "http://www.apartmanija.hr/apartmani/omisalj"

    };

    // next to process is je http://www.officialcharts.com/charts/dance-singles-chart/20091031/104
    public static final String STORE_LOC = "/tmp/apartmanija-hr";

    public static void main (String [] args){
        try{
            if (new File(STORE_LOC).exists()){
                System.out.println(STORE_LOC + " already exists. Quitting");
                return;
            }else{
                if (!new File(STORE_LOC).mkdirs()){
                    System.out.println("Can't create dir " + STORE_LOC);
                }
            }

            for (String one_location : LOCATION_HOME) {
                for (String url : getApartmentsUrls(one_location)) {
                    System.out.println("Processing " + url);
                    FileUtils.writeStringToFile(
                            new File(STORE_LOC + "/" + java.net.URLEncoder.encode(url, "UTF-8")),
                            Jsoup.connect(url).get().toString()
                    );
                }
            }

            System.out.println("Done");
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    // gets all apartments url in location page
    private static Set<String> getApartmentsUrls(String url) throws Exception{
        Set<String> toReturn = new HashSet<>();
        Document document = Jsoup.connect(url).get();

        for (Element el : document.body().getElementById("content_col_left").getElementsByClass("prop_cont")) {
            toReturn.add(el.getElementsByClass("prop_img").first().select("a").attr("href"));
        }
        for (Element el : document.body().getElementById("content_col_left").getElementsByClass("prop_cont featured")) {
            toReturn.add(el.getElementsByClass("prop_img").first().select("a").attr("href"));
        }

        System.out.println("Found " + toReturn.size() + " apartment urls");
        return toReturn;
    }
}
