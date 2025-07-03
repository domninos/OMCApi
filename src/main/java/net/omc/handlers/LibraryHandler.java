package net.omc.handlers;

import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import net.omc.OMCPlugin;
import net.omc.database.OMCDatabase;
import net.omc.util.Libraries;
import net.omc.util.OMCExec;
import org.bukkit.Bukkit;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LibraryHandler {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final OMCPlugin plugin;
    private String libsPath;

    private BukkitLibraryManager libraryManager;

    public LibraryHandler(OMCPlugin plugin) {
        this.plugin = plugin;
    }

    public void setLibraryPath(String path) {
        this.libsPath = path;
    }

    public void loadLibraries(OMCDatabase.Type type) throws ExecutionException, InterruptedException {
        switch (type) {
            case SQLITE:
                loadSQLiteLibraries().get();
                break;
            case POSTGRESQL:
                loadPostgresLib(true).get();
                break;
            case REDIS:
                loadRedisLib().get();
                break;
            case FLAT_FILE:
            default:
                break;
        }

        plugin.sendConsole(plugin.getDBMessageHandler().getLibraryLoaded(type.getLabel()));
    }

    public boolean isLibLoaded(OMCDatabase.Type type) {
        return type.isLoaded(plugin);
    }

    public boolean isLibLoaded() {
        return plugin.getDatabaseHandler().getAdapter() != null && plugin.getDatabaseHandler().getAdapter().getType().isLoaded(plugin);
    }

    public Future<Boolean> loadSQLiteLibraries() {
        return submitExec(() -> {
            Library sqlite = Library.builder()
                    .groupId("org{}xerial")
                    .artifactId("sqlite-jdbc")
                    .version("3.49.1.0")
                    .relocate("org{}sqlite", libsPath + "{}org{}xerial")
                    .build();

            libraryManager.loadLibrary(sqlite);

            Libraries.SQLITE.load(plugin.getDataFolder());
            return true;
        });
    }

    public Future<Boolean> loadPostgresLib(boolean installHikari) {
        return submitExec(() -> {
            if (installHikari)
                ensureHikari();

            Library postgres = Library.builder()
                    .groupId("org{}postgresql")
                    .artifactId("postgresql")
                    .version("42.7.7")
                    .relocate("org{}postgresql", libsPath + "{}org{}postgresql")
                    .build();

            libraryManager.loadLibrary(postgres);

            Libraries.POSTGRESQL.load(plugin.getDataFolder());
            return true;
        });
    }

    public Future<Boolean> loadRedisLib() {
        return submitExec(() -> {
            Library reactivestreams = Library.builder()
                    .groupId("org{}reactivestreams")
                    .artifactId("reactive-streams")
                    .version("1.0.4")
                    .build();

            Library projectreactor = Library.builder()
                    .groupId("io{}projectreactor")
                    .artifactId("reactor-core")
                    .version("3.6.6")
                    .build();

            Library lettuce = Library.builder()
                    .groupId("io{}lettuce")
                    .artifactId("lettuce-core")
                    .version("6.6.0.RELEASE")
                    .id("AlessioDP")
                    .repository("https://repo.alessiodp.com/releases/")
                    .relocate("io{}lettuce{}core", libsPath + "{}io{}lettuce{}core")
                    .build();

            libraryManager.loadLibrary(reactivestreams);
            Libraries.REACTIVE_STREAMS.load(plugin.getDataFolder());

            libraryManager.loadLibrary(projectreactor);
            Libraries.PROJECT_REACTOR.load(plugin.getDataFolder());

            libraryManager.loadLibrary(lettuce);
            Libraries.REDIS.load(plugin.getDataFolder());
            return true;
        });
    }

    public void ensureMainLibraries() {
        this.libraryManager = new BukkitLibraryManager(plugin);

        libraryManager.addMavenCentral();
        libraryManager.addSonatype();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                loadSQLiteLibraries().get();
            } catch (InterruptedException | ExecutionException e) {
                plugin.error("Something went wrong loading postgresql.", e);
            }
        });
    }

    // use hikari connection pool when dealing with SQL databases except SQLite (?)
    public void ensureHikari() {
        Library hikaricp = Library.builder()
                .groupId("com{}zaxxer")
                .artifactId("HikariCP")
                .version("6.3.0")
                .relocate("com{}zaxxer{}hikari", libsPath + "{}com{}zaxxer{}hikari")
                .build();

        libraryManager.loadLibrary(hikaricp);

        Libraries.HIKARICP.load(plugin.getDataFolder());

        plugin.sendConsole(plugin.getDBMessageHandler().getLibraryLoaded("HikariCP"));
    }

    public Future<Boolean> ensureJSON() {
        return submitExec(() -> {
            Library json = Library.builder()
                    .groupId("org{}json")
                    .artifactId("json")
                    .version("20250517")
                    .relocate("org{}json", libsPath + "{}org{}json")
                    .build();

            libraryManager.loadLibrary(json);

            Libraries.JSON.load(plugin.getDataFolder());

            plugin.sendConsole(plugin.getDBMessageHandler().getLibraryLoaded("JSON"));
            return true;
        });
    }

    public Future<Boolean> submitExec(OMCExec func) {
        return executor.submit(func::exec);
    }

    public void stopExecutor() {
        this.executor.shutdown();
    }
}