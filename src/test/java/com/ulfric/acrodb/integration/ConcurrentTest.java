package com.ulfric.acrodb.integration;

import com.ulfric.acrodb.Bucket;
import com.ulfric.acrodb.Document;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentTest {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(new FileUpdaterJob());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.submit(new FileUpdaterJob());
        executorService.shutdown();
    }
}

class FileUpdaterJob implements Runnable {

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName());
        Bucket bucket = new Bucket(Paths.get("src/test/resources"));
        Document document = bucket.openDocument("concurrentTest");

        for(int i = 0; i < 10; i++) {

            final int lineNum = i;

            try {
                Thread.sleep(1000);
                document.editAndWrite(ContentAppender.class, contentAppender -> {
                    contentAppender.appendLine(lineNum+"-"+ Thread.currentThread().getId());
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        bucket.save();
    }
}

class ContentAppender {
    private List<String> content;

    void appendLine(String line) {
        if(content == null) content = new ArrayList<>();
        content.add(line);
    }
}
