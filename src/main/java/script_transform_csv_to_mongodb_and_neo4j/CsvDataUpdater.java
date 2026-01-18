package script_transform_csv_to_mongodb_and_neo4j;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CsvDataUpdater {
    public static final String firstDatasetFolder= ConfigurationFileReader.checkAndGetProp("firstDatasetFolder");
    public static final String secondDatasetFolder=ConfigurationFileReader.checkAndGetProp("secondDatasetFolder");;
    public static  String transformedDatasetFolder=
            ConfigurationFileReader.getProperty("transformedDatasetFolder")!=null ?
            ConfigurationFileReader.getProperty("transformedDatasetFolder") :
            firstDatasetFolder.substring(0,firstDatasetFolder
            .substring(0,firstDatasetFolder.length()-2).lastIndexOf("\\")+1)+"TransformedDataset\\";
    public static final String metaMembersPath= secondDatasetFolder+"meta-members.csv";
    public static final String metaEventsPath= secondDatasetFolder+"meta-events.csv";
    public static final String metaGroupsPath= secondDatasetFolder+"meta-groups.csv";
    public static final String membersPath= firstDatasetFolder+"members.csv";
    public static final String groupsPath= firstDatasetFolder+"groups.csv";
    public static final String eventsPath= firstDatasetFolder+"events.csv";
    public static final String memberTopicsPath= firstDatasetFolder+"members_topics.csv";
    public static final String groupTopicsPath= firstDatasetFolder+"groups_topics.csv";
    public static final String transformedMembers=transformedDatasetFolder+"members.csv";
    public static final String transformedGroups=transformedDatasetFolder+"members.csv";
    public static final String transformedEvents=transformedDatasetFolder+"members.csv";
    public static final String transformedGroupTopics=transformedDatasetFolder+"groups_topics.csv";
    public static final String transformedMemberTopics=transformedDatasetFolder+"member_topics.csv";

    static List<String> readColumnValues(String csvPath, String column) throws Exception {
        try (CSVReader reader = new CSVReader(new FileReader(csvPath))) {
            String[] header = reader.readNext();
            int index = Arrays.asList(header).indexOf(column);

            if (index == -1)
                throw new RuntimeException("Column not found: " + column);

            List<String> values = new ArrayList<>();
            String[] row;
            while ((row = reader.readNext()) != null) {
                values.add(row[index]);
            }
            return values;
        }
    }

    static Map<String, String> buildIdMapping(
            String destinationCsv,
            String destinationIdColumn,
            List<String> newIds,
            int maxIds
    ) throws Exception {

        Map<String, String> mapping = new HashMap<>();
        Set<String> seen = new HashSet<>();
        Iterator<String> newIdIterator = newIds.iterator();

        try (CSVReader reader = new CSVReader(new FileReader(destinationCsv))) {
            String[] header = reader.readNext();
            int idIndex = Arrays.asList(header).indexOf(destinationIdColumn);

            String[] row;
            while ((row = reader.readNext()) != null && newIdIterator.hasNext()) {
                String oldId = row[idIndex];

                if (!seen.contains(oldId)) {
                    mapping.put(oldId, newIdIterator.next());
                    seen.add(oldId);

                    if (maxIds > 0 && mapping.size() >= maxIds)
                        break;
                }
            }
        }
        return mapping;
    }

    public static Map<String, String> updateIdsFromCsv(
            String sourceCsv,
            String sourceIdColumn,
            String destinationIdColumn,
            int maxIds,
            String... destinationCsvs
    ) throws Exception {

        if (destinationCsvs == null || destinationCsvs.length == 0)
            throw new RuntimeException("No destination CSVs provided");

        Files.createDirectories(Path.of(transformedDatasetFolder));

        // 1. Read new IDs (single-threaded)
        List<String> newIds = readColumnValues(sourceCsv, sourceIdColumn);

        // 2. Build mapping (single-threaded)
        Map<String, String> mapping = buildIdMapping(
                destinationCsvs[0],
                destinationIdColumn,
                newIds,
                maxIds
        );

        Map<String, String> sameFileReplacements;
        try (ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
            List<Future<?>> futures = new ArrayList<>();

            sameFileReplacements = new HashMap<>();

            // 3. Parallel CSV processing
            for (String csv : destinationCsvs) {
                String output = transformedDatasetFolder + Path.of(csv).getFileName();

                boolean sameFile = csv.equalsIgnoreCase(output);
                if (sameFile) {
                    output = output.replace(".csv", "_out.csv");
                    sameFileReplacements.put(csv, output);
                }

                String finalOutput = output;

                futures.add(executor.submit(() -> {
                    try {
                        applyMapping(csv, finalOutput, destinationIdColumn, mapping);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }));
            }

            // 4. Wait for ALL tasks
            for (Future<?> future : futures) {
                future.get();   // <-- THIS is the correct wait
            }

            executor.shutdown();
        }

        // 5. Replace original files (single-threaded, safe)
        for (Map.Entry<String, String> entry : sameFileReplacements.entrySet()) {
            Files.delete(Path.of(entry.getKey()));
            Files.move(Path.of(entry.getValue()), Path.of(entry.getKey()));
        }

        return mapping;
    }


    static void applyMapping(
            String inputCsv,
            String outputCsv,
            String idColumn,
            Map<String, String> mapping
    ) throws Exception {



        try (
                CSVReader reader = new CSVReader(new FileReader(inputCsv));

                CSVWriter writer = new CSVWriter(new FileWriter(outputCsv))
        ) {
            String[] header = reader.readNext();
            writer.writeNext(header);

            int index = Arrays.asList(header).indexOf(idColumn);

            String[] row;
            while ((row = reader.readNext()) != null) {
                String oldId = row[index];
                if (mapping.containsKey(oldId)) {
                    row[index] = mapping.get(oldId);
                }
                writer.writeNext(row);
            }
        }
    }

    public static void updateMemberIdsAtCSV() throws Exception {
        CsvDataUpdater.updateIdsFromCsv(CsvDataUpdater.metaMembersPath,"member_id","member_id",-1,CsvDataUpdater.membersPath,CsvDataUpdater.memberTopicsPath);
    }
    public static void updateEventIdsAtCSV() throws Exception {
        CsvDataUpdater.updateIdsFromCsv(CsvDataUpdater.metaEventsPath,"event_id","event_id",-1,CsvDataUpdater.eventsPath);
    }
    public static void updateGroupIdsAtCSV() throws Exception {
        CsvDataUpdater.updateIdsFromCsv(CsvDataUpdater.metaGroupsPath,"group_id","group_id",-1,CsvDataUpdater.groupsPath,transformedDatasetFolder+"members.csv",CsvDataUpdater.groupTopicsPath,CsvDataUpdater.eventsPath);
    }

    public static void updateIdsDirectlyFromCSV(){


        try {
            CsvDataUpdater.updateEventIdsAtCSV();
            CsvDataUpdater.updateMemberIdsAtCSV();
            CsvDataUpdater.updateGroupIdsAtCSV();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }





    class CsvMemberUpdater {
        void updateMembersTopicsCsv(
                String inputPath,
                String outputPath,
                Map<String, String> idMap
        ) throws Exception {

            try (
                    CSVReader reader = new CSVReader(new FileReader(inputPath));
                    CSVWriter writer = new CSVWriter(new FileWriter(outputPath))
            ) {
                String[] header = reader.readNext();
                writer.writeNext(header);

                int memberIdIndex = Arrays.asList(header).indexOf("member_id");

                String[] row;
                while ((row = reader.readNext()) != null) {
                    String oldId = row[memberIdIndex];

                    if (idMap.containsKey(oldId)) {
                        row[memberIdIndex] = idMap.get(oldId);
                    }

                    writer.writeNext(row);
                }
            }
        }

        void transformMemberAndMemberTopicsData() throws Exception {
            List<String> newIds = loadMetaMemberIds(metaMembersPath);

            Map<String, String> idMap = updateMembersCsv(
                    membersPath,
                    firstDatasetFolder + "members_transformed.csv",
                    newIds
            );

            updateMembersTopicsCsv(
                    memberTopicsPath,
                    firstDatasetFolder + "members_topics_transformed.csv",
                    idMap
            );
        }


        Map<String, String> updateMembersCsv(
                String membersPath,
                String outputPath,
                List<String> newIds
        ) throws Exception {

            Map<String, String> idMap = new HashMap<>();
            Set<String> seenOldIds = new HashSet<>();
            Iterator<String> newIdIterator = newIds.iterator();

            try (
                    CSVReader reader = new CSVReader(new FileReader(membersPath));
                    CSVWriter writer = new CSVWriter(new FileWriter(outputPath))
            ) {
                String[] header = reader.readNext();
                writer.writeNext(header);

                int memberIdIndex = Arrays.asList(header).indexOf("member_id");

                String[] row;
                while ((row = reader.readNext()) != null) {
                    String oldId = row[memberIdIndex];

                    if (!seenOldIds.contains(oldId) && newIdIterator.hasNext()) {
                        String newId = newIdIterator.next();
                        idMap.put(oldId, newId);
                        row[memberIdIndex] = newId;
                        seenOldIds.add(oldId);
                    } else if (idMap.containsKey(oldId)) {
                        row[memberIdIndex] = idMap.get(oldId);
                    }

                    writer.writeNext(row);
                }
            }
            return idMap;
        }


        List<String> loadMetaMemberIds(String metaPath) throws Exception {
            List<String> ids = new ArrayList<>();

            try (CSVReader reader = new CSVReader(new FileReader(metaPath))) {
                String[] header = reader.readNext();

                int idIndex = Arrays.asList(header).indexOf("member_id");

                String[] row;
                while ((row = reader.readNext()) != null) {
                    ids.add(row[idIndex]);
                }
            }
            return ids;
        }
    }
}