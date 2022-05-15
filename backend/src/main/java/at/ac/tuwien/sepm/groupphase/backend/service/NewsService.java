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
     * @return created news entry
     */
    News createNews(NewsDto news) throws IOException;


    /**
     *  Gets all the available news entries.
     *
     *  @return all news entries
     */
    List<NewsDto> getAll();

    /**
     * Gets a news entry by id.
     *
     * @param id of the related news element
     * @return corresponding news element
     */
    NewsDto getById(Long id);
}
