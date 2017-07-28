class QuestionSelector {

    private static SELECT_FROM_BACK = 0.1
    private static SELECT_NEW = 0.33
    private static SELECT_NEGATIVE = 0.33
    private static START_SCORE = 3


    private def db
    private def backlist
    private Random r = new Random()

    def selectQuestion() {

        def selectedQ = [:]

        // first see if we should select from our backlist
        if(!backlist.isEmpty() && r.nextFloat() < SELECT_FROM_BACK) {
            selectedQ['q'] = backlist.randomEntry(r)
            selectedQ['reason'] = 'backlist'
        // then see if we should select a new voc
        } else if(r.nextFloat() < SELECT_NEW && db.hasNew()) {
            selectedQ['q'] = db.randomNew()
            selectedQ['reason'] = 'new'
        // then see if we should select a voc with negative score
        } else if(r.nextFloat() < SELECT_NEGATIVE && db.hasNegative()) {
            selectedQ['q'] = db.randomNegative()
            selectedQ['reason'] = 'negative'
        // just roulettewheel select from all entries
        } else {
            selectedQ['q'] = roulettewheelSelectFromDB(db, r)
            selectedQ['reason'] = 'wheel'
        }

        return selectedQ

    }

    private def roulettewheelSelectFromDB(def database, def random) {

        def db = database.getDatabase()
        def allWords = db.keySet()
        def scoreList = allWords.collect{
            def baseScore = db[it].incorrect - db[it].correct + START_SCORE
            if(baseScore < 1)
                baseScore = 1
            return baseScore
        }
        def maxScore = scoreList.sum()
        def sample = random.nextInt(maxScore)
        for(int i=0; i<allWords.size(); i++) {
            def w = allWords[i]
            def s = scoreList[i]
            sample -= s
            if(sample <=0)
                return db[w]
        }

    }

}
