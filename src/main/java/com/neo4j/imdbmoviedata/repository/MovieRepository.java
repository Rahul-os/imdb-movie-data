package com.neo4j.imdbmoviedata.repository;

import com.neo4j.imdbmoviedata.entity.Movie;
import com.neo4j.imdbmoviedata.entity.MovieDetailsDto;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface MovieRepository extends Neo4jRepository<Movie, String> {

    //@Query("MATCH (m:Movie) RETURN  m.id AS id, m.title AS title, m.description AS description, m.revenue AS revenue,m.year AS year,m.ids AS ids, m.runtime AS runtime, m.rating AS rating, m.votes AS votes, m.genres AS genres")
    @Query("MATCH (m:Movie) RETURN  m.title AS title, m.description AS description, m.revenue AS revenue,m.year AS year,m.ids AS ids, m.runtime AS runtime, m.rating AS rating, m.votes AS votes")
    List<Movie> getAllMoviesInDB();

    //@Query("CREATE (movie:Movie {title: $movieTitle})-[:IN]->(genre:Genres {type: $genreType})")
//    @Query("MATCH (movie:Movie {title: $movieTitle})\n" +
//            "WITH movie\n +" +
//            "UNWIND movie.genres AS genre\n" +
//            "MERGE (genre:Genres {type: genre})\n" +
//            "CREATE (movie)-[:IN]->(genre)")
    @Query("MATCH (movie:Movie {title: $movieTitle})\n" +
            "UNWIND $genres AS genre\n" +
            "MERGE (g:Genres {type: genre})\n" +
            "CREATE (movie)-[:IN]->(g)")                                          // this is the excat query that is not creating duplicate relaionships !!!
    void createGenreRelationship(List<String> genres, String movieTitle);
    //    @Query("CREATE (person:Person {actors: $actor})-[:ACTED_IN]->(movie:Movie {title: $movieTitle})")
//    @Query("MATCH (movie:Movie {title: $movieTitle})\n" +
//            "WITH movie\n" +
//            "UNWIND movie.actors AS actor\n" +
//            "MERGE (person:Person {name: actor})\n" +
//            "CREATE (movie)-[:ACTED_IN]->(person)")
    @Query("MATCH (movie:Movie {title: $movieTitle})\n" +
            "WITH movie\n" +
            "UNWIND $actors AS actor\n" +
            "MERGE (a:Person {name: actor})\n" +
            "CREATE (a)-[:ACTED_IN]->(movie)")
    void crateActorRelationship(List<String> actors, String movieTitle);

    //@Query("CREATE (person:Person {director: $directorName})-[:DIRECTED]->(movie:Movie {title: $movieTitle})")
//    @Query("MATCH (movie:Movie {title: $movieTitle})\n" +
//            "WITH movie\n" +
//            "UNWIND movie.directors AS director\n" +
//            "MERGE (person:Person {name: director})\n" +
//            "CREATE (movie)-[:DIRECTED]->(person)")                            //this cominaiton of queries for 3 methods are creating a seperate movie node for each relation which i don't want. so i have updated the query.
    @Query("MATCH (movie:Movie {title: $movieTitle})\n" +
            "WITH movie\n" +
            "UNWIND $directors AS director\n" +
            "MERGE (d:Person {name: director})\n" +
            "CREATE (d)-[:DIRECTED]->(movie)")
    void createDirectorRelationship(List<String> directors, String movieTitle);

    //get movie details by title.
//    @Query("MATCH (movie:Movie {title: $movieTitle}) " +
//            "OPTIONAL MATCH (movie)-[:ACTED_IN]-(actor:Person) " +
//            "OPTIONAL MATCH (movie)-[:DIRECTED]-(director:Person) " +
//            "OPTIONAL MATCH (movie)-[:IN]-(genre:Genres) " +
//            "RETURN movie, COLLECT(DISTINCT actor) AS actors, COLLECT(DISTINCT director) AS directors, COLLECT(DISTINCT genre) AS genres")
//    @Query("MATCH (movie:Movie {title: $movieTitle}) " +
//            "OPTIONAL MATCH (movie)<-[:ACTED_IN]-(actors:Person) " +
//            "OPTIONAL MATCH (movie)<-[:DIRECTED]-(directors:Person) " +
//            "OPTIONAL MATCH (movie)-[:IN]->(genres:Genres) " +
//            "RETURN movie, collect(actors.name) AS actors, collect(directors.name) AS directors, collect(genres.type) AS genres")
//   the above query is returning duplicate actors,directors. so used below query and it worked .but not giving genres
//    @Query("MATCH (movie:Movie {title: $movieTitle}) " +
//            "OPTIONAL MATCH (movie)<-[:ACTED_IN]-(actors:Person) " +
//            "WITH DISTINCT movie, COLLECT(DISTINCT actors.name) AS actors " +
//            "OPTIONAL MATCH (movie)<-[:DIRECTED]-(directors:Person) " +
//            "WITH DISTINCT movie, actors, COLLECT(DISTINCT directors.name) AS directors " +
//            "OPTIONAL MATCH (movie)-[:IN]->(genres:Genres) " +
//            "RETURN DISTINCT movie, actors, directors, COLLECT(DISTINCT genres.type) AS genres")
//    @Query("MATCH (movie:Movie {title: $movieTitle}) " +
//            "OPTIONAL MATCH (movie)<-[:ACTED_IN]-(actors:Person) " +
//            "WITH movie, COLLECT(DISTINCT actors.name) AS actors " +
//            "OPTIONAL MATCH (movie)<-[:DIRECTED]-(directors:Person) " +
//            "WITH movie, actors, COLLECT(DISTINCT directors.name) AS directors " +
//            "OPTIONAL MATCH (movie)-[:IN]->(genres:Genres) " +
//            "WITH movie, actors, directors, COLLECT(DISTINCT genres.type) AS genres " +
//            "RETURN movie, actors, directors, genres")
//    @Query("MATCH (movie:Movie {title: $movieTitle})\n" +
//            "OPTIONAL MATCH (movie)<-[:ACTED_IN]-(actors:Person)\n" +
//            "OPTIONAL MATCH (movie)<-[:DIRECTED]-(directors:Person)\n" +
//            "OPTIONAL MATCH (movie)-[:IN]->(genres:Genres)\n" +
//            "RETURN DISTINCT movie, COLLECT(DISTINCT actors.name) AS actors, COLLECT(DISTINCT directors.name) AS directors, COLLECT(DISTINCT genres.type) AS genres")
    @Query("MATCH (movie:Movie {title: $movieTitle}) " +
            "OPTIONAL MATCH (movie)<-[:ACTED_IN]-(actors:Person) " +
            "OPTIONAL MATCH (movie)<-[:DIRECTED]-(directors:Person) " +
            "OPTIONAL MATCH (movie)-[:IN]->(genres:Genres) " +
            "RETURN movie, COLLECT(DISTINCT actors.name) AS actors, COLLECT(DISTINCT directors.name) AS directors, COLLECT(DISTINCT genres.type) AS genres")
    MovieDetailsDto getMovieDetails(String movieTitle);
}