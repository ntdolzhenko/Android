import java.util.Random

fun main() {
    // переменные, которые не "зависят" от текующей цены акции и остаются такими на протяжении всей работы программы:
    val people: MutableList<Person> = mutableListOf()                             // массив пользователей make_People
    val stakanPokupka: MutableList<PokupkaClass> = mutableListOf()              // стакан покупки
    val stakanProdazha: MutableList<ProdazhaClass> = mutableListOf()             // стакан продажи

    for (i in 0..99) {                                                             // создаём пользователей и заносим их в общий массив пользователей
        val a = Person(id = i, high = makeHigh(), low = makeLow())
        people.add(a)
    }
////////////////////////
    val cur: Int = priceGraph()

    for (i in 0 until people.size) {                                               //заполняем наши стаканы ставками
        decision(i, stakanPokupka, stakanProdazha, people, cur)
    }

    for (i in stakanPokupka) {                                                    // сам процесс торгов
        for (p in stakanProdazha) {
            val result = canSell(p, cur)
            if (result) {
                if (p.price == i.price) {

                    if (i.count == p.count) {

                        val hedzhArr : Array<Int> = arrayOf(0, 1)                                                      //
                        val hRand : Int = hedzhArr[(Math.random() * hedzhArr.size).toInt()]                            //
                        if (hRand == 1){                                                                               //
                            val h = h(i.person_id, people)                                                             //блок хеджирования
                            val l = l(i.person_id, people)                                                             //
                            stakanProdazha.add(ProdazhaClass(i.person_id, (l * cur).toInt(), i.count, 2))       //
                            stakanProdazha.add(ProdazhaClass(i.person_id, (h * cur).toInt(), i.count, 1))       //
                        }

                        people[i.person_id].money -= cur * i.count                  // меняем балансы каждого пользователя, который учавствовал в ставке
                        people[i.person_id].amount += i.count
                        people[p.person_id].money += cur * p.count
                        people[p.person_id].amount -= p.count

                        stakanPokupka.remove(i)
                        stakanProdazha.remove(p)
                        break
                    }
                    else if (i.count < p.count) {

                        val hedzhArr : Array<Int> = arrayOf(0, 1)                                                      //
                        val hRand : Int = hedzhArr[(Math.random() * hedzhArr.size).toInt()]                                  //
                        if (hRand == 0){                                                                               //
                            val h = h(i.person_id, people)                                                             //блок хеджирования
                            val l = l(i.person_id, people)                                                             //
                            stakanProdazha.add(ProdazhaClass(i.person_id, (l * cur).toInt(), i.count, 2))        //
                            stakanProdazha.add(ProdazhaClass(i.person_id, (h * cur).toInt(), i.count, 1))        //
                        }

                        people[i.person_id].money -= cur * i.count                             // меняем балансы каждого пользователя, который учавствовал в ставке
                        people[i.person_id].amount += i.count
                        people[p.person_id].money += cur * i.count
                        people[p.person_id].amount -= i.count

                        stakanPokupka.remove(i)                                            // закрываем ставку с покупкой
                        p.count -= i.count
                        break
                    }
                    else if (i.count > p.count) {

                        val hedzhArr : Array<Int> = arrayOf(0, 1)                                                     //
                        val hRand : Int = hedzhArr[(Math.random() * hedzhArr.size).toInt()]                          //
                        if (hRand.compareTo(1) == 0){                                                                               //
                            val h = h(i.person_id, people)                                                             //блок хеджирования
                            val l = l(i.person_id, people)                                                             //
                            stakanProdazha.add(ProdazhaClass(i.person_id, (l * cur).toInt(), p.count, 2))      //
                            stakanProdazha.add(ProdazhaClass(i.person_id, (h*cur).toInt(), p.count, 1 ))       //
                        }

                        people[i.person_id].money -= cur * p.count                                         // меняем балансы каждого пользователя, который учавствовал в ставке
                        people[i.person_id].amount += p.count
                        people[p.person_id].money += cur * p.count
                        people[p.person_id].amount -= p.count

                        stakanProdazha.remove(p)

                        i.count -= p.count
                        continue
                    }
                }
                if (i.price != p.price) continue
            }
            if (!result) continue
        }
        continue
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/////////////////КЛАССЫ/////////////////
class Person (val id : Int, var money : Int = 1000000,  var amount : Int = 1000, var high : List<Int>, var low : List<Int>)    //класс пользователя

class PokupkaClass ( val person_id : Int, val price : Int, var count : Int ) //класс покупки

class ProdazhaClass (val person_id : Int, val price : Int, var count : Int, val type : Byte)  // класс продажи ( тип продаж: 0-обычная, 1-специальная с "верхней ценой", 2-специальная с "нижней ценой"


/////////////////ФУНКЦИИ///////////////

fun IntRange.random() =
    Random().nextInt((endInclusive + 1) - start) +  start          // функция которая возвращает рандомное число в заданном диапазоне целых чисел


fun makeHigh(): MutableList<Int> {                                     // создаём верхний диапазон пользователя
    val high : MutableList<Int> = mutableListOf()
    high.add( (10..20).random() )
    high.add( (10..20).random() )
    (high.sorted()).toList()
    return high
}
fun makeLow(): MutableList<Int> {                                      // создаём нижний диапазон пользователя
    val low : MutableList<Int> = mutableListOf()
    low.add( (10..20).random() )
    low.add( (10..20).random() )
    (low.sorted()).toList()
    return low
}

fun canSell (p : ProdazhaClass, cur: Int) : Boolean {                             // метод, который проверяет, "работает" ли ставка при текущей рыночной цене(специальные ставки не всегла работают)
    var res = true
    if ( ((p.type == 1.toByte()) and (p.price >= cur)) or ((p.type == 2.toByte()) and (p.price >= cur)) ) res = false
    return res
}
fun decision ( i : Int, stakan_pokupka : MutableList<PokupkaClass>, stakan_prodazha: MutableList<ProdazhaClass> , people : MutableList<Person>, cur : Int) {
    val decision = (0..1).random()
    if (decision == 0){
        val price : Int = (cur * ( 1F - ((0..20).random()).toFloat()/100)).toInt()                // функция, которая создаёт ставки каждого пользователя
        val count : Int = (1 until 1000).random()
        val stavka = PokupkaClass(people[i].id, price, count)
        stakan_pokupka.add(stavka)
    }
    if (decision == 1){
        val price : Int  = (cur * ( 1F + ((0..20).random()).toFloat()/100)).toInt()
        val count : Int = (1 until people[i].amount).random()
        val stavka = ProdazhaClass(people[i].id, price, count, 0)
        stakan_prodazha.add(stavka)
    }
}

 fun priceGraph (): Int {                                   // функция изменения цены акции
     val priceRandom : Int = (-50..50).random()
     var price = 100
     price += priceRandom
     if (price <=0) price = 100
     return price
 }


fun h(i : Int, people : MutableList<Person>): Float {
    return ( (( people[i].high[0] .. people[i].high[1] ).random()).toFloat() ) / 10
}
fun l(i : Int, people : MutableList<Person>): Float {
    return ( (( people[i].low[0] .. people[i].low[1] ).random()).toFloat() ) / 10
}
