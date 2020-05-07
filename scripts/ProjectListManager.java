import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ProjectListManager {
    
    public static void main(String[] args) throws Exception {
        System.out.println("== Project List Manager ==");
        BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));
        String projectInfo = reader.readLine();
        reader.close();
        
        reader = new BufferedReader(new FileReader(new File(args[1])));
        ArrayList<String> projects = new ArrayList<String>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith(projectInfo.split(";")[0])) {
                System.out.println("[PLM] Updating project properties");
                projects.add(projectInfo);
            } else {
				if (isProject(line))
					projects.add(line);
			}
        }
        if (!projects.contains(projectInfo))
            System.out.println("[PLM] Adding project to the list");
            projects.add(projectInfo);
        reader.close();
        
        PrintWriter writer = new PrintWriter(new FileOutputStream(new File(args[1])));
        for (int i = 0; i < projects.size(); i++) {
            writer.println(projects.get(i));
        }
        writer.close();
        System.out.println("[PLM] Done!");
    }
    
    private static boolean isProject(String line) {
        return !"".equals(line) || !line.isEmpty() || !"\n".equals(line) || !" ".equals(line); 
    }

}
