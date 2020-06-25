const FgRed = "\x1b[31m"
const FgGreen = "\x1b[32m"
const FgWhite = "\x1b[37m"

const checkMark = `${FgGreen}✓${FgWhite}`
const errorMark = `${FgRed}✕${FgWhite}`
const allGood = `${FgGreen}All good =D${FgWhite}`
const epicFail = `${FgRed}Your code sucks!${FgWhite}`

const errorMessage = "Ooops, you are a bad developer..."

const errorList = []

const describe = (message, callback) => {
    console.log(message)
    callback()
    errorList.length === 0 ? console.log(allGood) : console.log(epicFail)
    for(const e of errorList) {
        console.log(e)
    }
}

const it = (message, assertions) => {
    try{
        assertions()
        console.log(checkMark, message)
    } catch(e){
        if(e.message !== errorMessage) throw e
        console.log(errorMark, message)
        errorList.push(e)
    }
}

const expects = thing => {
    return {
        toEqual: other => assert(other, thing, other.equals ? other.equals(thing) : other == thing),
        toBe: other => assert(other, thing, other === thing),
        toBeTruthy: () => assert(true, !!thing, !!thing),
        toBeFalsey: () => assert(false, !!!thing, !!!thing)
    }
}

function assert(expected, actual, assertion){
    if(!assertion) {
        const error = new Error(errorMessage)
        error.expected = expected
        error.actual = actual
        throw error
    }
}

module.exports = {
    describe,
    it,
    expects
}