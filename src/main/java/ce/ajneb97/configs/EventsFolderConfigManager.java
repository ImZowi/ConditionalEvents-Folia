package ce.ajneb97.configs;

<<<<<<< HEAD

=======
>>>>>>> 4ff4fd5d45b5469f47175220762728224b08f43a
import ce.ajneb97.ConditionalEvents;
import ce.ajneb97.configs.model.CommonConfig;

public class EventsFolderConfigManager extends DataFolderConfigManager{

    public EventsFolderConfigManager(ConditionalEvents plugin, String folderName) {
        super(plugin, folderName);
    }

    @Override
    public void createFiles() {
        new CommonConfig("more_events.yml",plugin,folderName,false).registerConfig();
    }

    @Override
    public void loadConfigs() {
<<<<<<< HEAD

=======
>>>>>>> 4ff4fd5d45b5469f47175220762728224b08f43a
    }

    @Override
    public void saveConfigs() {
<<<<<<< HEAD

    }


}
=======
    }
}
>>>>>>> 4ff4fd5d45b5469f47175220762728224b08f43a
