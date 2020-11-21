import java.util.Random

fun main() {
    var cur = 100
    val people: MutableList<Person> = mutableListOf()                             // массив пользователей
    val stakan : MutableList<Stavka> = mutableListOf()

    for (i in 0..99) {                                                             // создаём пользователей и заносим их в общий массив
        val a = Person(id = i, high = makeHigh(), low = makeLow())
        people.add(a)
    }

    while (true){
        for (i in people) {
            val decision = (0..1).random()
            var type = 0
            if (decision == 1) {
                type = 1
            }
            val st = Stavka(i.id, (cur * (1F - ((0..20).random()).toFloat() / 100)).toInt(), (1 until 1000).random(), type)
            if (stakan.size == 0) {
                stakan.add(st)
            }
            if (stakan.size != 0) {
                for (p in stakan)
                {
                    if (st.type == 0)
                    {
                        if ((p.type == 1) or (p.type == 2) or (p.type == 3))
                        {
                            val result = canSell(p, cur)
                            if (result)
                            {
                                if (st.price >= p.price)
                                {
                                    if (p.count == st.count) {

                                        hedge(i, p, cur, people, stakan)
                                        changeData(p, st, people)
                                        cur = p.price

                                        stakan.remove(p)
                                        break
                                    }
                                    else if (p.count < st.count) {

                                        hedge(i, p, cur, people, stakan)
                                        changeData(p, st, people)
                                        cur = p.price

                                        st.count -= p.count
                                        stakan.remove(p)
                                        continue
                                    }
                                    else if (p.count > st.count) {

                                        hedge(i, p, cur, people, stakan)
                                        changeData(p, st, people)
                                        cur = p.price

                                        p.count -= st.count
                                        break
                                    }
                                }
                            }
                        }
                    }

                    if (st.type == 1)
                    {
                        if(p.type == 0)
                        {
                            if (st.price <= p.price)
                            {
                                if (p.count == st.count) {

                                    hedge(i, p, cur, people, stakan)
                                    changeData(p, st, people)
                                    cur = p.price

                                    stakan.remove(p)
                                    break
                                }
                                else if (p.count < st.count) {

                                    hedge(i, p, cur, people, stakan)
                                    changeData(p, st, people)
                                    cur = p.price

                                    st.count -= p.count
                                    stakan.remove(p)
                                    continue
                                }
                                else if (p.count > st.count) {

                                    hedge(i, p, cur, people, stakan)
                                    changeData(p, st, people)
                                    cur = p.price

                                    p.count -= st.count
                                    break
                                }
                            }
                        }
                    }
                }
                if (st.count != 0) stakan.add(st)
            }
        }
    }
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/////////////////КЛАССЫ/////////////////
class Person (val id : Int, var money : Int = 1000000,  var amount : Int = 1000, var high : List<Int>, var low : List<Int>)    //класс пользователя

class Stavka (val person_id : Int, val price : Int, var count : Int, val type : Int)  // класс ставки 0-покупка 1-продажа 2-high 3-low

/////////////////ФУНКЦИИ///////////////

fun IntRange.random() =
    Random().nextInt((endInclusive + 1) - start) +  start      // функция которая возвращает рандомное число в заданном диапазоне целых чисел

fun makeHigh(): MutableList<Int> {                                  // создаём верхний диапазон пользователя
    val high : MutableList<Int> = mutableListOf()
    high.add( (10..20).random() )
    high.add( (10..20).random() )
    (high.sorted()).toList()
    return high
}

fun makeLow(): MutableList<Int> {                           // создаём нижний диапазон пользователя
    val low : MutableList<Int> = mutableListOf()
    low.add( (10..20).random() )
    low.add( (10..20).random() )
    (low.sorted()).toList()
    return low
}

fun canSell (p : Stavka, cur: Int) : Boolean {            // функция, которая проверяет, "работает" ли ставка при текущей рыночной цене(специальные ставки не всегла работают)
    var res = true
    if ( ((p.type == 2) and (p.price >= cur)) or ((p.type == 3) and (p.price >= cur)) ) res = false
    return res
}

fun hedge(i: Person, p: Stavka, cur: Int, people: MutableList<Person>, stakan : MutableList<Stavka>)    // хеджирование
{
    val hedzhArr: Array<Int> = arrayOf(0, 1)
    val hRand: Int = hedzhArr[(Math.random() * hedzhArr.size).toInt()]
    if (hRand == 1) {
        val h = ( (( people[i.id].high[0] .. people[i.id].high[1] ).random()).toFloat() ) / 10
        val l = ( (( people[i.id].low[0] .. people[i.id].low[1] ).random()).toFloat() ) / 10
        stakan.add(Stavka(i.id, (l * cur).toInt(), p.count, 3))
        stakan.add(Stavka(i.id, (h * cur).toInt(), p.count, 2))
    }
}

fun changeData(p: Stavka, st: Stavka, people: MutableList<Person>)    // функция, которая меняет данные пользователей после закрытия(частичного закрытия) ставки
{
    people[p.person_id].money += p.price * p.count
    people[p.person_id].amount -= p.count
    people[st.person_id].money -= p.price * p.count
    people[st.person_id].amount += p.count
}

