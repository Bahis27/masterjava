package ru.javaops.masterjava.upload;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

@Slf4j
public class CityProcessor {
    private static final int NUMBER_THREADS = 4;
    private static CityDao cityDao = DBIProvider.getDao(CityDao.class);
    private ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);

    /*
     * return added cities
     */
    public List<City> process(InputStream is, int chunkSize) throws XMLStreamException {
        log.info("Start processing with chunkSize=" + chunkSize);

        List<Future<List<City>>> chunkFutures = new ArrayList<>();

        List<City> chunk = new ArrayList<>(chunkSize);
        val processor = new StaxStreamProcessor(is);

        while (processor.doUntil(XMLEvent.START_ELEMENT, "City")) {
            String id = processor.getAttribute("id");
            String name = processor.getText();
            final City city = new City(id, name);
            chunk.add(city);
            if (chunk.size() == chunkSize) {
                addChunkFutures(chunkFutures, chunk);
                chunk = new ArrayList<>(chunkSize);
            }
        }

        if (!chunk.isEmpty()) {
            addChunkFutures(chunkFutures, chunk);
        }

        List<City> added = new ArrayList<>();
        chunkFutures.forEach(future -> {
            try {
                added.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                log.error(future.toString(), e);
            }
        });

        return added;
    }

    private void addChunkFutures(List<Future<List<City>>> chunkFutures, List<City> chunk) {
        Future<List<City>> future = executorService.submit(() -> cityDao.insertAndGetAddedCities(chunk));
        chunkFutures.add(future);
        log.info("Submit chunk");
    }
}
