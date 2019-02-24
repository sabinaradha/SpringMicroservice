package com.microservice.moviecatalogservice.resources;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.microservice.moviecatalogservice.models.CatalogItem;
import com.microservice.moviecatalogservice.models.Movie;
import com.microservice.moviecatalogservice.models.Rating;
import com.microservice.moviecatalogservice.models.UserRating;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {
	
	@Autowired
	RestTemplate restTemplate;
	
	@RequestMapping("/{userId}")
	public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){
//		RestTemplate restTemplate = new RestTemplate();
		//get all rated movie IDs
		UserRating ratings = restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/"+ userId, UserRating.class);
				
		
		return ratings.getUserRating().stream().map(rating -> {
			//For each movie Id, call movie info service and get details
			Movie movie = restTemplate.getForObject("http://movie-info-service/movies/"+rating.getMovieId(), Movie.class);
			
			//Put them all together
			return new CatalogItem(movie.getName(), "Desc", rating.getRating());   
		})
		.collect(Collectors.toList());
		
		
	}

}
