class VocabularyDatabase {

    private static final DATE_FORMAT = "d/M/yyyy H:m:s"

    private def db = [:]
    private def backlist = new Backlist()
    private def selector = new QuestionSelector(db : this, backlist : backlist)
    private def random = new Random()

    private VocabularyDatabase() {}

    static def loadFromVocFile(def file) {
        def db = new VocabularyDatabase()
        new File(file).readLines().eachWithIndex{line, linecounter ->
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
                println "### Error parsing line ${linecounter + 1}: ${e.getMessage()} ###"
            }
        }
        return db
    }

    def loadPreviousResults(def file) {
        def f = new File(file)
        if(!f.exists())
            return
        f.readLines().eachWithIndex{ line, linecounter ->
            try {
                def parsed = line.trim().split(";")
                def q = parsed[0]
                def correct = parsed[3] == "true"
                def lastAsked = Date.parse(DATE_FORMAT, parsed[4])
                if (!db.containsKey(q)) {
                    println "### Previous voc '${q}' not found in vocabulary database. Ignoring ###"
                    return
                }
                if (correct)
                    db[q].correct++
                else
                    db[q].incorrect++
                db[q].lastAsked = lastAsked
            } catch(Exception e) {
                println "### Error parsing log line ${linecounter + 1}: ${e.getMessage()} ###"
            }
        }
        // // TMP - debug
        // db.each { k, v ->
        //   println "${k}: ${v.correct}/${v.incorrect}"
        // }
    }

    def getDatabase() { db }

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

    def overallRecord(){
      db.values().sum{q -> q.correct - q.incorrect }
    }

    def positiveRated(){
      db.count{ _,q -> q.isPositive() }
    }

    def countNews(){
      db.count{_,q ->  q.lastAsked == null }
    }

    def hasNew(){
      countNews() > 0
    }

    def randomNew() {
        def news = db.findAll{_,q -> q.lastAsked == null }.values()
        news[random.nextInt(news.size())]
    }

    def hasNegative() { db.any{_,q -> q.isNegative() } }

    def randomNegative() {
        def incorrects = db.findAll{_,q -> q.isNegative() }.values()
        incorrects[random.nextInt(incorrects.size())]
    }

    private void log(def logfile, def question, def answer, def isCorrect) {
        def log = "${question.q};${question.a};$answer;$isCorrect;${new Date().format(DATE_FORMAT)}\n"
        new File(logfile) << log
    }

}
