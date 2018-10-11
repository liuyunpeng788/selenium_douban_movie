package com.fosun.stargazer.personal.selenium;

import com.fosun.stargazer.personal.selenium.dto.entity.Actor;
import com.fosun.stargazer.personal.selenium.dto.entity.Movie;
import com.fosun.stargazer.personal.selenium.dto.entity.MovieType;
import com.fosun.stargazer.personal.selenium.dto.relationship.MovieTypeShip;
import com.fosun.stargazer.personal.selenium.repository.neo4j.ActorRepository;
import com.fosun.stargazer.personal.selenium.repository.neo4j.MovieRepository;
import com.fosun.stargazer.personal.selenium.repository.neo4j.MovieTypeRepository;
import com.fosun.stargazer.personal.selenium.repository.neo4j.WriterRepository;
import com.fosun.stargazer.personal.selenium.service.Neo4jService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashSet;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SeleniumApplicationTests {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private MovieTypeRepository movieTypeRepository;

	@Autowired
	private Neo4jService neo4jService;



	@Test
	public void contextLoads() {
	}

	@Test
	public void addMovie(){
	    MovieType movieType = new MovieType();
        MovieTypeShip movieTypeShip = new MovieTypeShip();
        Actor actor = new Actor();

        actor.setChName("张三");
        actor.setEngName("zhang san");
        actor.setRepresentativeWork("晴雯");

        Movie movie = new Movie();
        movie.setAlias("test");
        movie.setName("test");
        movie.setYear(2018);
        movie.setCategory("电影");

        movieTypeShip.setBetterProportion("46.6%");
        movieTypeShip.setMovie(movie);
        movieTypeShip.setMovieType(movieType);

        movieType.setName("动作片");

        Movie qMovie = movieRepository.findByNameAndYear(movie.getName(),movie.getYear());
        neo4jService.updateId(movie,qMovie);

        Actor qActor = actorRepository.queryActorsByNameAndAlias(actor.getChName(),actor.getEngName());
        neo4jService.updateId(actor,qActor);

        MovieType qMovieType = movieTypeRepository.queryMovieTypeByName(movieType.getName());
//       if(null != qMovieType){ movieType.setId(qMovieType.getId());}
        neo4jService.updateId(movieType,qMovieType);

        movieRepository.save(movie);

	}

	@Test
    public void findMovieByNameAndYear(){
        Movie movie = movieRepository.findByNameAndYear("test",2018);
        System.out.println(movie);
    }



}
