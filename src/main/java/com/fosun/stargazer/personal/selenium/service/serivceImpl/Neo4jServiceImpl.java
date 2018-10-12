package com.fosun.stargazer.personal.selenium.service.serivceImpl;

import com.fosun.stargazer.personal.selenium.dto.entity.*;
import com.fosun.stargazer.personal.selenium.dto.relationship.ActorShip;
import com.fosun.stargazer.personal.selenium.dto.relationship.MovieTypeShip;
import com.fosun.stargazer.personal.selenium.dto.relationship.ReleasePlaceShip;
import com.fosun.stargazer.personal.selenium.repository.neo4j.*;
import com.fosun.stargazer.personal.selenium.service.Neo4jService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Set;

@Service("Neo4jService")
public class Neo4jServiceImpl implements Neo4jService{

    private static Logger logger = LoggerFactory.getLogger("Neo4jServiceImpl");

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private MovieTypeRepository movieTypeRepository;

    @Autowired
    private WriterRepository writerRepository;

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private ReleasePlaceRepository releasePlaceRepository;

    @Autowired
    private DirectorRepository directorRepository;

    @Autowired
    private MovieTypeShipRepository movieTypeShipRepository;

    @Autowired
    private ActorShipRepository actorShipRepository;

    @Override
    public Long insertMovie(final Movie movie){
        Long id = -1L;
        try{
            if(null == movie){
                logger.info("insert failure as movie is null")
                ;return id;
            }
            updateMovieId(movie);
            id = movieRepository.save(movie).getId();
            logger.info("insert Movie,id:"+ id);
        }catch (Exception ex){
            logger.error(ExceptionUtils.getStackTrace(ex));
        }
        return id;
    }

    @Override
    public void updateMovieId(final Movie movie){
        if(null == movie){
            logger.info("param movie is null");
            return ;
        }
        Movie qMovie = movieRepository.findByNameAndYear(movie.getName(),movie.getYear());
        updateId(movie,qMovie);

        logger.info("updateMovieId actorShips....");
        //演员信息
        Set<ActorShip> actorShips =  movie.getActorShips();
        if(null != actorShips){
            actorShips.parallelStream().forEach(x->{
                Actor actor = x.getActor();
                if(null != actor && null != actor.getChName()){
                    Actor newActor = actorRepository.queryActorsByNameAndAlias(actor.getChName(),actor.getEngName());
                    updateId(actor,newActor);
                    ActorShip newActorShip = actorShipRepository.getActorShipByActorAndMovie(actor.getChName(),movie.getName(),movie.getYear()); //更新角色id
                    updateId(x,newActorShip);
                }
            });
        }

        logger.info("updateMovieId producers.....");
        //制片人信息
        Set<Producer> producers = movie.getProducers();
        if(null != producers){
            producers.parallelStream().forEach(x->{
                Producer producer = producerRepository.queryProducerByName(x.getChName(),x.getEngName());
                updateId(x,producer);
            });
        }

        logger.info("updateMovieId writers.....");
        //编剧信息
        Set<Writer> writers = movie.getWriters();
        if(null != writers){
            writers.parallelStream().forEach(x->{
                Writer writer = writerRepository.queryWritersByNameAndAlias(x.getChName(),x.getEngName());
                updateId(x,writer);
            });
        }

        logger.info("updateMovieId releasePlaceShips.....");
        //首发信息
        Set<ReleasePlaceShip> releasePlaceShips = movie.getReleasePlaceShips();
        if(null != releasePlaceShips){
            releasePlaceShips.parallelStream().forEach(x->{
                ReleasePlace releasePlace = x.getReleasePlace();
                if(null != releasePlace && null != releasePlace.getName()){
                    ReleasePlace newReleasePlace = releasePlaceRepository.queryReleasePlaceByName(releasePlace.getName());
                    if(null != newReleasePlace){ releasePlace.setId(newReleasePlace.getId());}
                }
            });
        }

        logger.info("updateMovieId movieTypeShips.....");
        //所属类型信息
        Set<MovieTypeShip> movieTypeShips = movie.getMovieTypeShips();
        if(null != movieTypeShips){
            movieTypeShips.parallelStream().forEach(x->{
                MovieType movieType = x.getMovieType();
                if(null != movieType && null != movieType.getName()){
                    MovieType newMovieType = movieTypeRepository.queryMovieTypeByName(movieType.getName());
                    updateId(newMovieType,newMovieType);
                }
                //添加更新id
                MovieTypeShip newMovieTypeShip = movieTypeShipRepository.getMovieTypeShipByMovieAndMovieType(movie.getName(),movie.getYear(),movieType.getName());
                updateId(x,newMovieTypeShip);
            });
        }

        logger.info("updateMovieId director.....");
        //导演信息
        Director director = movie.getDirector();
        if(null != director && null != director.getChName()){
            Director newDirector = directorRepository.queryDirectorByNameAndAlias(director.getChName(),director.getEngName());
            updateId(director,newDirector);
        }
    }


    /**
     * 利用发射，调用对象的set方法，修改Id值
     * @param obj 待修改的对象
     * @param pObj 数据库中查出来的对象
     */
    @Override
   public void updateId(Object obj,final Object pObj){
       try{

            if(null == pObj){
                logger.info("update id....,but param pObj is null");
                return ;
            }
           logger.info("update obj:" + obj.toString());
            Class[] parameterTypes = new Class[1];
            parameterTypes[0] = obj.getClass().getDeclaredField("id").getType();
            Method getMethod = pObj.getClass().getDeclaredMethod("getId");
            Method method = obj.getClass().getDeclaredMethod("setId",parameterTypes);
            Long id = (Long)getMethod.invoke(pObj,new Object[0]);
            logger.info("updated ....id:" + id);
            method.invoke(obj,id);
       }catch (Exception ex){
           ex.printStackTrace();
       }
   }

   public static void test(final Movie m){
//       Movie movie = m;
       m.setYear(1323);
       System.out.println(m);
   }
   public static void main(String[] args){

       Movie movie = new Movie();
       movie.setAlias("test");
       movie.setName("test");
       movie.setYear(2018);
       movie.setCategory("电影");
       System.out.println("before update id :" + movie);

       test(movie);

       Movie pMovie = new Movie();
       pMovie.setId(110L);
//       updateId(movie,pMovie);
       System.out.println("after update id :" + movie);

   }
}
