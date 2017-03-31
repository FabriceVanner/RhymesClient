package learning;

/**
 * Created by Fabrice Vanner on 16.09.2016.
 */
public class MashineLearning {

    /**
     * TODO: output output object oder PhEntry ausgeben
     * TODO: verschiedene Testfälle deninieren:
     *          Subset von entries verwenden
     *          verschiedene listen von wörtern mit gewünschten Ergebnisfaktoren(bereich: AUTO auf LAUTLOS soll sich zwischen 0.8 und 0.9 zueinander reimen
     *          alle listen durchiterieren
     *          Ergebnisse speichern (bzw. abweichungen) abweichungen aufadieren, durch anzahl von tests teilen
     *          -> eine stellschraube des algo in intervalllen ändern --> bzw. alle möglichen einstellungskombinationen durchprobieren
     *          abweichungen mit algo-einstellungen abspeichern
     *          beste ergebnisse als einstellungsset speichern

     ################################################
     Pseudocode
     ################################################
     SettingDef{
     String name
     double min-val
     double max-val
     double step
     SettingDef (name, min-val, max-val, step){
     this.
     }
     }

     SettingsSet{
     //i-var or Map for each setting, the algorithm is going to use
     Map settingMap(key=name,value=val); // besser wären primitive values
     setSetting(name, val){
     settingMap.set(name, val)
     }
     }

     Wordpair{
     String wordOne;
     String wordTwo;
     Double toleratedDifference
     Double destinationScore
     }

     SettingsResult{
     SettingsSet settingsSet
     Map differences (key=wordPair, value=difference)
     double overallDiffernceSum

     SettingsResult(SettingsSet settingsSet){
     this...
     }

     add(Wordpair wordPair, Double difference){
     this.differences.add(wordPair, difference);
     }

     calcDifferenceSum(){
     for differences.iterator{
     overallDiffernceSum += difference;
     }
     overallDiffernceSum = overallDiffernceSum/numberOfMapEntries
     }
     }

     Calculation{
     SettingDef settingDef
     Calculation nextCalculation
     static Algorithm algorithm
     static LIST <SettingsResult> settingsResults
     static LIST <WordPair> wordPairs

     setStatics(Algorithm algorithm, LIST <SettingsResult> settingsResults, LIST <WordPair> wordPairs){this...}
     Calculation (SettingDef settingDef) {this...	}

     addCalculation(Calculation nextCalculation){
     if(this.nextCalculation==null){
     this.nextCalculation = nextCalculation;
     }else{
     this.nextCalculation.addCalculation(nextCalculation)
     }
     }

     calc(SettingsSet settingsSet){
     if(nextCalculation==NULL){ // here is the last child --> calculation starts
     for-loop(i = from setting-def.min-val with setting-def.step to setting-def.max-val){
     settingsSet.setSetting(this.settingDef.name, i)
     algorithm.setSettings(settingsSet);
     boolean lost=false;
     SettingsResult settingsResult;
     for(wordpair: wordPairs){
     Double calculatedRhymeScore = algorithm.runLearningTask(wordPair);
     Double difference =  destinationScore - calculatedRhymeScore
     if (abs(difference)<wordpair.toleratedDifference){
     if(settingsResult==null)settingsResult = new SettingsResult(settingsSet.clone())
     settingsResult.add(wordPair,abs(difference))
     }else{
     lost=true;
     break;
     }
     }
     if (!lost){
     settingsResult.calcDifferenceSum();
     settingsResults.add(settingsResult)
     }
     }
     }else{ // durchreichen zum nächsten Child
     for(int i = from setting-def.min-val with setting-def.step to setting-def.max-val){
     settingsSet.setSetting(this.settingDef.name, i)
     calculation.calc(settingsSet)
     }
     }
     }
     }
     }





     MAIN CLASS{
     LIST SettingDef settingDefs
     LIST SettingsResult settingsResults
     LIST WordPair wordpairs;
     Algorithm algorithm

     void MAIN{
     settingDefs.add(new SETTING())
     wordpairs = parseWordpairs()

     Calculation calculation = new calculation(settingDef)
     calculation.setStatics(algorithm, settingsResults, wordPairs){
     for(SettingDef settingDef : settingDefs){
     if(calculation==null){
     calculation = new Calculation(settingDef)
     }else{
     calculation.addCalculation(new Calculation(settingDef));
     }
     }
     calculation.calc(new SettingsSet());
     SettingsSet bestSet = findbestSettingsSet();
     STORE in xml or serialize: bestSet
     }

     LIST <WordPair> parseWordpairs(Textfile){
     textfile: wordOne - wordTwo - toleratedDifference - destinationScore
     }



     SettingsSet findbestSettingsSet()
     LOOP for settingsResults{
     find lowest overallDiffernceSum --> best setting
     return settings-set
     }
     }
     }



     METHODs in rhyme-Algorithm:

     void setSettings(settingsSet);
     double runLearningTask(wordPair){
     Map map = runQuery(wordPair.wordOne)
     find wordPair.wordTwo in map
     return its rhymevalue
     }




     */
}
