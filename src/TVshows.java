public class TVshows {

    private final String name;
    private final String originalLanguage;
    private final Integer voteCount;
    private final Double voteAverage;
    private final String firstAirDate;
    private final String lastAirDate;
    private final Boolean inProduction;
    private final Double popularity;
    private final String genres;

    public TVshows(String name, String originalLanguage, Integer voteCount, Double voteAverage, String firstAirDate, String lastAirDate, Boolean inProduction, Double popularity, String genres) {
        this.name = name;
        this.originalLanguage = originalLanguage;
        this.voteCount = voteCount;
        this.voteAverage = voteAverage;
        this.firstAirDate = firstAirDate;
        this.lastAirDate = lastAirDate;
        this.inProduction = inProduction;
        this.popularity = popularity;
        this.genres = genres;
    }

    public String getName() {
        return name;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public String getLastAirDate() {
        return lastAirDate;
    }

    public Boolean getInProduction() {
        return inProduction;
    }

    public Double getPopularity() {
        return popularity;
    }

    public String getGenres() {
        return genres;
    }

    //a method to return a CSV string representation of the object
    public String toCSVString() {
        return String.join(",", name, originalLanguage, String.valueOf(voteCount),
                String.valueOf(voteAverage), firstAirDate, lastAirDate,
                String.valueOf(inProduction), String.valueOf(popularity), genres);
    }
}
