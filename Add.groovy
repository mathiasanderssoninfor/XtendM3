  /**
  * README
  * This extension is being used to add record in table EXTRFI - V2
  *
  * Name: EXT007MI.Add
  * Description: Add record in EXTRFI table
  * Date         Changed By                         Description
  * 20231220     Mathias Andersson                  Initial release
  */
    
  import java.time.LocalDate;
  import java.time.LocalTime;
  import java.time.format.DateTimeFormatter;
  import java.time.Instant;
  
  public class Add extends ExtendM3Transaction {
    private final MIAPI mi;
    private final ProgramAPI program;
    private final DatabaseAPI database;
    private int inCONO;
    private String inITNO;
    private String inSENO;
    private String inKEY1;
    private String inEPCN;
    private String inRGDT;
    private String inRGTM;
    private String inRGID;
    
    public Add(MIAPI mi, ProgramAPI program, DatabaseAPI database) {
      this.mi = mi;
      this.program = program;
      this.database = database;
    }
    
    public void main() {
      // - Get current company
      inCONO = program.LDAZD.CONO.toString() as int;   
      
      // - Get API input values
      inEPCN = (!mi.inData.get("EPCN").toString()?.trim()) ? "" : mi.inData.get("EPCN").trim();
      inSENO = (!mi.inData.get("SENO").toString()?.trim()) ? "" : mi.inData.get("SENO").trim();
      inITNO = (!mi.inData.get("ITNO").toString()?.trim()) ? "" : mi.inData.get("ITNO").trim();
      inKEY1 = (!mi.inData.get("KEY1").toString()?.trim()) ? "" : mi.inData.get("KEY1").trim();
              
      // - Add record to EXTRFI
      boolean recordCreated = false;
      DBAction dBquery = database.table("EXTRFI").index("00").selection("EXCONO","EXEPCN").build();
      DBContainer EXTRFI = dBquery.createContainer();
      EXTRFI.set("EXCONO", inCONO);
      EXTRFI.set("EXITNO", inITNO);
      EXTRFI.set("EXSENO", inSENO.toLong());
      EXTRFI.set("EXKEY1", inKEY1);
      EXTRFI.set("EXEPCN", inEPCN);
      
      if(!dBquery.read(EXTRFI)){      
        EXTRFI.set("EXRGDT", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger());
        EXTRFI.set("EXRGTM", LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger());
        EXTRFI.set("EXRGID", program.getUser());
        if(dBquery.insert(EXTRFI)){
          recordCreated = true;
          }
        } else {
          mi.error("Record already exist");
          return;
        }
      if(recordCreated){
        mi.outData.put("STAT", "OK");
      } else {
        mi.outData.put("STAT", "NOK");
      }
      mi.write();
    }
  }
