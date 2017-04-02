class VocabularyDatabase {

    private static final DATE_FORMAT = "d/M/yyyy H:m:s"

    private def db = [:]
    private def backlist = new Backlist()
    private def selector = new QuestionSelector(db : db, backlist : backlist)

    private VocabularyDatabase() {}

    static def loadFromVocFile(def file) {
        def db = new VocabularyDatabase()
        def linecounter = 0
        new File(file).readLines().each{ line ->
            def parsed = line.trim().split(";")
            def q = parsed[0].trim()
            def a = parsed[1].trim()
            linecounter++
            if(db.db.containsKey(q)) {
                println "### Duplicate voc $q. Ignoring ###"
            }
            try {
                db.db[q] = new Voc(q: q, a: a)
            } catch(Exception e) {
                println "### Error parsing line $linecounter: ${e.getMessage()} ###"
            }
        }
        return db
    }

    def loadPreviousResults(def file) {
        def f = new File(file)
        if(!f.exists())
            return
        f.readLines().each{ line ->
            def parsed = line.trim().split(";")
            def q = parsed[0]
            def correct = parsed[3] as boolean
            def lastAsked = Date.parse(DATE_FORMAT, parsed[4])
            if(!db.containsKey(q)) {
                println "### Previous voc '${q}' not found in vocabulary database. Ignoring ###"
                return
            }
            if(correct)
                db[q].correct++
            else
                db[q].incorrect++
            db[q].lastAsked = lastAsked
        }
    }

    def nextQuestion(){ selector.selectQuestion() }

    def answered(def question, def answer, def isCorrect, def logfile){

        if(isCorrect){
            db[question.q].correct++
            if(backlist.onList(question.q))
                backlist.removeFromList(question.q)
        }
        else {
            db[question.q].incorrect++
            backlist.addToList(question)
        }

        log(logfile, question, answer, isCorrect)

    }

    def size(){ db.size() }

    private void log(def logfile, def question, def answer, def isCorrect) {
        def log = "${question.q};${question.a};$answer;$isCorrect;${new Date().format(DATE_FORMAT)}\n"
        new File(logfile) << log
    }

}
