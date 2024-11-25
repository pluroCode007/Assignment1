import java.io.*;
import java.util.*;


public class Main {
    public static void main(String[] args) throws IOException {
        ArrayList<TVshows> tvShowsList = new ArrayList<>();
        getTVShows(tvShowsList, "src/TVShows.csv");

        // Task 1: Top 50 shows by rating (already implemented in original code)
        findTop50Shows(tvShowsList, "src/output/top_50_rated_shows.csv");

        // Task 2: Top 50 shows per language (already implemented in original code)
        findTop50ShowsPerLanguage(tvShowsList);

        // Task 3: Shows with vote count > 15000
        findShowsWithHighVoteCount(tvShowsList, "src/output/high_vote_count_shows.csv");

        // Task 4: Top 50 shows between 2010-2019
        findTop50ShowsBetweenDates(tvShowsList, "src/output/top_shows_2010_2019.csv");

        // Task 5: Top 50 shows in production
        findTop50InProductionShows(tvShowsList, "src/output/top_shows_in_production.csv");

        // Task 6: Top 50 single-word title shows by popularity
        findTop50SingleWordTitleShows(tvShowsList, "src/output/top_single_word_shows.csv");

        // Task 7: Top 50 shows by genre
        findTop50ShowsByGenre(tvShowsList, "src/output/top_shows_by_genre.csv");
    }


    private static void getTVShows(ArrayList<TVshows> tvShowsArray, String filename) {
        try (BufferedReader buff = new BufferedReader(new FileReader(filename))) {
            buff.readLine();

            String line;
            while ((line = buff.readLine()) != null) {
                try {

                    List<String> fields = new ArrayList<>();
                    StringBuilder currentField = new StringBuilder();
                    boolean inQuotes = false;

                    for (int i = 0; i < line.length(); i++) {
                        char c = line.charAt(i);

                        if (c == '"') {
                            //I spent a week figuring out why it was not parcing only to find out I needed the help of Indians to explain different approaches...
                            // this part is to handle quote character
                            if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                                // this part is to then handle double quote inside quoted string - add single quote
                                currentField.append('"');
                                i++; //I saw rows where I needed to skip next quote
                            } else {
                                //aaaand to toggle quote mode
                                inQuotes = !inQuotes;
                            }
                        } else if (c == ',' && !inQuotes) {
                            //then the end of field - add to list
                            fields.add(currentField.toString().trim());
                            currentField.setLength(0);
                        } else {
                            //then regular character - add to current field
                            currentField.append(c);
                        }
                    }

                    fields.add(currentField.toString().trim());

                    if (fields.size() < 9) {
                        System.err.println("Skipping malformed line: insufficient columns");
                        continue;
                    }

                    String name = fields.get(0);
                    String originalLanguage = fields.get(1);
                    Integer voteCount = Integer.parseInt(fields.get(2));
                    Double voteAverage = Double.parseDouble(fields.get(3));
                    String firstAirDate = fields.get(4);
                    String lastAirDate = fields.get(5);
                    Boolean inProduction = Boolean.parseBoolean(fields.get(6));
                    Double popularity = Double.parseDouble(fields.get(7));
                    String genres = fields.get(8);

                    TVshows show = new TVshows(name, originalLanguage, voteCount, voteAverage,
                            firstAirDate, lastAirDate, inProduction, popularity, genres);

                    tvShowsArray.add(show);

                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    System.err.println("Error parsing line: " + line);
                    System.err.println("Detailed error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Task 1: Find top shows based on ratings and write them to a file
        private static void findTop50Shows(List<TVshows> tvShowsList, String filename) {

            tvShowsList.sort(Comparator.comparingDouble(TVshows::getVoteAverage).reversed());
            List<TVshows> top50 = tvShowsList.subList(0, Math.min(50, tvShowsList.size()));

            try (BufferedWriter buff = new BufferedWriter(new FileWriter(filename))) {
                buff.write("Name,Original_Language,Vote_Count,Vote_Average\n");     // write new CSV file title

                for (TVshows tvShow : top50) {
                    buff.write(tvShow.toCSVString() + "\n");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Written top 50 shows to: " + filename);
        }

        // Task 2: Find top shows based on ratings and original language
        private static void findTop50ShowsPerLanguage(List<TVshows> tvShowsList) {
        Map<String, List<TVshows>> showsByLanguage = new HashMap<>(); //make a new empty map

        //for loop to group TV shows by language
        for (TVshows show : tvShowsList) {
            String language = show.getOriginalLanguage(); // Language = key
            showsByLanguage.putIfAbsent(language, new ArrayList<>()); // If the map is empty, add the key and the array
            showsByLanguage.get(language).add(show);
        }

        //for loop to find top 50 rated shows in each language and write to separate files
        for (Map.Entry<String, List<TVshows>> entry : showsByLanguage.entrySet()) {
            String language = entry.getKey();
            List<TVshows> shows = entry.getValue();

            //to sort shows by rating
            shows.sort(Comparator.comparingDouble(TVshows::getVoteAverage).reversed());
            List<TVshows> top50 = shows.subList(0, Math.min(50, shows.size()));

            //to write to a language-specific CSV file as the task instructed
            String outputFilename = "src/output/task2." + language + ".csv"; // E.g., task2.en.csv

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename))) {
                writer.write("Name,Original_Language,Vote_Count,Vote_Average,First_Air_Date,Last_Air_Date,In_Production,Popularity,Genres\n");
                for (TVshows tvShow : top50) {
                    writer.write(tvShow.toCSVString() + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Written top rated shows for language '" + language + "' to: " + outputFilename);
        }
    }


        // Task 3: Find shows with vote count > 15000
        private static void findShowsWithHighVoteCount(List<TVshows> tvShowsList, String filename) {
            List<TVshows> highVoteCountShows = tvShowsList.stream()
                    .filter(show -> show.getVoteCount() > 15000)
                    .toList();

            try (BufferedWriter buff = new BufferedWriter(new FileWriter(filename))) {
                buff.write("Name,Original_Language,Vote_Count,Vote_Average\n");
                for (TVshows show : highVoteCountShows) {
                    buff.write(show.toCSVString() + "\n");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Total shows with vote count > 15000: " + highVoteCountShows.size());
            System.out.println("Written to: " + filename);
        }


    // Task 4: Find top 50 shows between 2010-2019
        private static void findTop50ShowsBetweenDates(List<TVshows> tvShowsList, String filename) {
            String startDate = "2010-01-01";
            String endDate = "2019-12-31";

            //to filter shows within the date range
            List<TVshows> showsBetweenDates = new ArrayList<>();
            for (TVshows i : tvShowsList) {
                String firstAirDate = i.getFirstAirDate();
                String lastAirDate = i.getLastAirDate();

                if ((firstAirDate.compareTo(startDate) >= 0) && (lastAirDate.compareTo(endDate) <= 0)) {
                    showsBetweenDates.add(i);
                }
            }

            //to sort by vote average in descending order
            showsBetweenDates.sort((a, b) -> Double.compare(b.getVoteAverage(), a.getVoteAverage()));

            //to limit to top 50 shows
            List<TVshows> top50Shows = new ArrayList<>();
            if (showsBetweenDates.size() > 50) {
                for (int i = 0; i < 50; i++) {
                    top50Shows.add(showsBetweenDates.get(i));
                }
            } else {
                top50Shows = showsBetweenDates;
            }

            try (BufferedWriter buff = new BufferedWriter(new FileWriter(filename))) {
                buff.write("Name,Original_Language,First_Air_Date,Last_Air_Date,Vote_Average\n");
                for (TVshows show : top50Shows) {
                    buff.write(show.getName() + "," +
                            show.getOriginalLanguage() + "," +
                            show.getFirstAirDate() + "," +
                            show.getLastAirDate() + "," +
                            show.getVoteAverage() + "\n");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Written top shows between 2010-2019 to: " + filename);
        }


    // Task 5: Find top 50 shows in production
    private static void findTop50InProductionShows(List<TVshows> tvShowsList, String filename) {
        //to filter shows that are in production
        List<TVshows> inProductionShows = new ArrayList<>();
        for (TVshows i : tvShowsList) {
            if (i.getInProduction()) {
                inProductionShows.add(i);
            }
        }

        //to sort by vote average in descending order
        inProductionShows.sort((a, b) -> Double.compare(b.getVoteAverage(), a.getVoteAverage()));

        //to limit to top 50 shows
        List<TVshows> top50Shows = new ArrayList<>();
        if (inProductionShows.size() > 50) {
            for (int i = 0; i < 50; i++) {
                top50Shows.add(inProductionShows.get(i));
            }
        } else {
            top50Shows = inProductionShows;
        }

        try (BufferedWriter buff = new BufferedWriter(new FileWriter(filename))) {
            buff.write("Name,Original_Language,In_Production,Vote_Average,Popularity\n");
            for (TVshows show : top50Shows) {
                buff.write(show.getName() + "," +
                        show.getOriginalLanguage() + "," +
                        show.getInProduction() + "," +
                        show.getVoteAverage() + "," +
                        show.getPopularity() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Written top shows in production to: " + filename);
    }


    // Task 6: Find top 50 single-word title shows by popularity
    private static void findTop50SingleWordTitleShows(List<TVshows> tvShowsList, String filename) {
        // Step 1: Find shows with single-word titles
        List<TVshows> singleWordTitleShows = new ArrayList<>();
        for (TVshows i : tvShowsList) {
            //to check if the title has only one word by splitting it on spaces -- found this regex online
            if (i.getName().split("\\s+").length == 1) {
                singleWordTitleShows.add(i);                     //to add it to the list if it's a single-word title
            }
        }

        // Step 2: Sort the shows by popularity (most popular first)
        singleWordTitleShows.sort((a, b) -> Double.compare(b.getPopularity(), a.getPopularity()));

        // Step 3: Pick the top 50 most popular shows
        List<TVshows> top50Shows = new ArrayList<>();
        if (singleWordTitleShows.size() > 50) {
            //if there are more than 50, just take the top 50
            for (int i = 0; i < 50; i++) {
                top50Shows.add(singleWordTitleShows.get(i));
            }
        } else {
            //if there are 50 or fewer, just use the whole list
            top50Shows = singleWordTitleShows;
        }

        // Step 4: Write the top shows to a file
        try (BufferedWriter buff = new BufferedWriter(new FileWriter(filename))) {
            // Add the header row for the CSV
            buff.write("Name,Original_Language,Popularity,Vote_Average\n");

            //for loop to add each show's details to the file
            for (TVshows i : top50Shows) {
                buff.write(i.getName() + "," +
                        i.getOriginalLanguage() + "," +
                        i.getPopularity() + "," +
                        i.getVoteAverage() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Written top single-word title shows to: " + filename);
    }


    // Task 7: Find top 50 shows by genre
    private static void findTop50ShowsByGenre(List<TVshows> tvShowsList, String filename) {
        Map<String, List<TVshows>> showsByGenre = new HashMap<>();

        for (TVshows i : tvShowsList) {
            String[] genres = i.getGenres().split(",");
            for (String j : genres) {
                j = j.trim();
                showsByGenre.putIfAbsent(j, new ArrayList<>());
                showsByGenre.get(j).add(i);
            }
        }

        try (BufferedWriter buff = new BufferedWriter(new FileWriter(filename))) {
            buff.write("Genre,Top_Show_Name,Original_Language,Vote_Average,Popularity\n");

            for (Map.Entry<String, List<TVshows>> entry : showsByGenre.entrySet()) {
                String genre = entry.getKey();
                List<TVshows> genreShows = entry.getValue();

                genreShows.sort(Comparator.comparingDouble(TVshows::getVoteAverage).reversed());
                TVshows topShow = genreShows.get(0);

                buff.write(genre + "," + topShow.getName() + "," +
                        topShow.getOriginalLanguage() + "," +
                        topShow.getVoteAverage() + "," +
                        topShow.getPopularity() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Written top shows by genre to: " + filename);
    }
}