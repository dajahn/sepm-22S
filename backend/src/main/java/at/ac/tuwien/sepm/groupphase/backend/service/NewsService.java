package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;

import java.io.IOException;
import java.util.List;

public interface NewsService {

    /**
     *  Creates a new News Entry.
     *
     * @param news to create
     * @param file related file
     * @return created news entry
     */
    News createNews(NewsDto news, File file) throws IOException;


    /**
     *  Gets all the available news entries.
     *
     *  @return all news entries
     */
    List<News> getAll();
}
