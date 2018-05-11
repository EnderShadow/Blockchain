package matt.kotlin.blockchain

import java.io.Serializable
import java.security.MessageDigest
import java.util.*
import matt.kotlin.blockchain.ImmutableByteArray.Companion.digest
import matt.kotlin.blockchain.ImmutableByteArray.Companion.update
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream

object Blockchain
{
    var head: Block? = null
        private set
    
    fun addBlock(data: Serializable)
    {
        head = Block(head, data)
    }
}

class Block(val priorBlock: Block?, val data: Serializable)
{
    val priorHash: ImmutableByteArray = priorBlock?.hash() ?: ImmutableByteArray(ByteArray(8))
    
    fun hash(): ImmutableByteArray
    {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(priorHash)
        val baos = ByteArrayOutputStream()
        ObjectOutputStream(baos).use {it.writeObject(data)}
        return ImmutableByteArray(md.digest(baos.toByteArray()))
    }
}

class ImmutableByteArray(data: ByteArray)
{
    companion object
    {
        fun MessageDigest.update(input: ImmutableByteArray) = update(input.source)
        fun MessageDigest.digest(input: ImmutableByteArray) = digest(input.source)
    }
    
    private val source = data.copyOf()
    
    operator fun get(index: Int) = source[index]
    operator fun iterator() = source.iterator()
    val size = source.size
    
    override fun equals(other: Any?): Boolean
    {
        if(other === null || other !is ImmutableByteArray)
            return false
        return Arrays.equals(source, other.source)
    }
    
    override fun hashCode() = Arrays.hashCode(source)
}