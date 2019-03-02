package sample;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.LinkedHashSet;
import java.util.Set;

public class Controller {
    @FXML
    TextField webSiteTextField;
    @FXML
    TextField domainTextField;

    static Thread thisThread;

    FileChooser fileChooser;

    public void initialize(){
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(File.separator));
    }

    @FXML
    void onButtonAddPressed() throws IOException{
        Window stage = webSiteTextField.getScene().getWindow();

        fileChooser.setTitle("save dialog");
        fileChooser.setInitialFileName("Broken-URL.xls");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel", "*xls"));

        try {
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                fileChooser.setInitialDirectory(file.getParentFile());
                Pair<Elements, Elements> pair = ThreadsCreate.getBroken();
                writeIntoExcel(file.getPath(), pair.getKey(), pair.getValue());
                ThreadsCreate.clear();
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private void writeIntoExcel(String File, Elements brokenPages, Elements brokenPages_Parent ) throws IOException {
        Workbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("Broken Pages");

        for (int i = 0; i < brokenPages.size(); i++){
            Row row = sheet.createRow(i);
            Cell page = row.createCell(0);
            page.setCellValue(brokenPages.get(i).tagName());
            Cell parent = row.createCell(1);
            parent.setCellValue(brokenPages_Parent.get(i).tagName());
        }
        FileOutputStream fo = new FileOutputStream(File);
        try {
            book.write(fo);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            book.close();
            fo.close();
        }

    }


    @FXML
    void onButtonPressed() throws IOException {

        thisThread = Thread.currentThread();
        Thread thread = new Thread(new ThreadsCreate(webSiteTextField.getText(), domainTextField.getText()));
        thread.setDaemon(true);
        thread.start();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/sample/Download.fxml"));
        Parent root = loader.load();
        Stage stage = Main.curStage;
        Main.SwitchScenes(root);
    }

}

class ThreadsCreate implements Runnable{

    static boolean state = false;
    static Set<Pair<String, String>> set = new LinkedHashSet<>();
    static int count = 0;
    private static Elements brokenPages = new Elements();
    private static Elements brokenPages_Parent = new Elements();
    static String domain;
    String webSite;
    ThreadsCreate(String webSite, String domain){
        this.domain = domain;
        this.webSite = webSite;
    }
    public static void clear(){
        brokenPages.clear();
        brokenPages_Parent.clear();
    }

    public static Pair<Elements, Elements> getBroken(){
        return new Pair<Elements, Elements>(brokenPages, brokenPages_Parent);

    }

    @Override
    public void run() {
        state = false;
        long start = System.currentTimeMillis();
        set.add(new Pair<>(webSite, webSite));
        Parser parser = new Parser(set, "1");
        count++;
        parser.Parse(webSite, webSite);
        Thread thread_1 = new Thread(parser);
        Thread thread_2 = new Thread(new Parser(set, "2"));
        Thread thread_3 = new Thread(new Parser(set, "3"));
        Thread thread_4 = new Thread(new Parser(set, "4"));
        Thread thread_5 = new Thread(new Parser(set, "5"));
        thread_1.setDaemon(true);
        thread_2.setDaemon(true);
        thread_3.setDaemon(true);
        thread_4.setDaemon(true);
        thread_5.setDaemon(true);
        thread_1.start();
        thread_2.start();
        thread_3.start();
        thread_4.start();
        thread_5.start();
        try{
            thread_1.join();
            thread_2.join();
            thread_3.join();
            thread_4.join();
            thread_5.join();
        }catch (InterruptedException e){
            System.out.println("Interrupted");
        }
        long end = System.currentTimeMillis();
        System.out.println("time - " + (end - start));
        System.out.println("Всего ссылок " + set.size());
        System.out.println("Count " + count);
        System.out.println("Broken pages " + brokenPages.size());
        count = 0;
        set.clear();
        state = true;
    }

    static synchronized boolean getState(){
        return state;
    }
    static synchronized Pair<String, Pair<String, Integer>> getURL(){
        if(count < set.size()) {
            String[] array_url = new String[set.size()];
            String[] array_parent = new String[set.size()];
            int k=0;
            for(Pair<String, String> i: set){
                array_url[k] = i.getKey();
                array_parent[k++] = i.getValue();
            }
            return new Pair<>(array_parent[count], new Pair<>(array_url[count++], count)); //ВЕРНУТЬ ПАРУ ПАР
        }else
            return null;
    }

    static void addBrokenLink(Element e, Element e_par){
        brokenPages.add(e);
        brokenPages_Parent.add(e_par);
    }
    static synchronized void setSet(Set<Pair<String, String>> e){
        for(Pair<String, String> i:e) {
            boolean flag = false;
            for (Pair<String, String> j : set) {
                if(i.getKey().equals(j.getKey())){
                    flag = true;
                    break;
                }
            }
            if(!flag)
                set.add(i);
        }
    }

    static synchronized void Output(String s){
        System.out.println(s);
    }

}



class Parser implements Runnable {
    Set<Pair<String, String>> set;
    Set<Pair<String, String>> children = new LinkedHashSet<>();
    Document doc;
    String threadName;
    int numb;
    Parser(Set<Pair<String, String>> set, String threadName) {
        this.set = set;
        this.threadName = threadName;
    }

    @Override
    public void run() {
        while(true) {
            ThreadsCreate.Output(threadName + " is Alive");
            Pair<String, Pair<String, Integer>> pair = ThreadsCreate.getURL();
            if (pair == null) {
                System.out.println(threadName + " is Dying");
                break;
            }
            else{
                String URL = pair.getValue().getKey();
                numb = pair.getValue().getValue();
                String URL_Parent = pair.getKey();
                Parse(URL, URL_Parent);
            }
        }
    }

    void Parse(String url, String parent) {
        boolean error = false;
        boolean connected = false;
        try {
            if (!url.startsWith("#")) {
                doc = Jsoup.connect(ProperLink(url)).get();
                connected = true;
            }
        } catch (HttpStatusException ex1) {
            //Битые ссылки
            ThreadsCreate.Output("BrokenPage");
            //Запись ссылок в список сломанных
            Element e = new Element(url);
            Element e_par = new Element(parent);
            ThreadsCreate.addBrokenLink(e, e_par);
            error = true;
            ThreadsCreate.Output("!!!!!!!!!!!!!!!! " + url + " !!!!!!!!!!!!!!!!");
        } catch (EOFException ex2) {
            ThreadsCreate.Output("In catch EOF");
            error = true;
        } catch (IOException ex3) {
            ThreadsCreate.Output("In IOException");
            error = true;
        } finally {
            if (!error && connected) {
                Elements links = doc.select("a[href]");
                for (Element link :
                        links) {
                    if (!link.attr("href").startsWith("#") && isProperPage(link)) {
                        String pLink = ProperLink(link.attr("href"));
                        children.add(new Pair<>(pLink, url));
                    }
                }
            }
            //Вывод информации для норм дебага {1: hse.ru...}
            ThreadsCreate.setSet(children);
            ThreadsCreate.Output(numb + " : " + url);
        }
    }

    //Проверка на то, что ссылка ведёт именно на hse.ru/...
    private boolean isProperPage (Element e){
        return e.attr("href").contains(ThreadsCreate.domain);
    }

    //Проверка на корректность ссылки в HTML (в случае некорректности, фиксит и переходит)
    private String ProperLink (String e){
        String url = e;
        char[] arr = url.toCharArray();
        if (arr.length != 0) {
            if(arr[url.length() - 1] != '/') {
                url = url.concat("/");
                arr = url.toCharArray();
            }
            if (arr[0] == '/' && arr[1] == '/')
                url = "https:" + url;
            else if (arr[0] == '/')
                url = "https:/" + url;
            else if(arr[4] != 's' && url.substring(0,4).equals("http"))
                url = "https:" + url.substring(5);
        }
        if (url.length() != 0)
            return url;
        else
            return e;
    }
}