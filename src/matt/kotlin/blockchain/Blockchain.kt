package matt.kotlin.blockchain

import java.io.Serializable
import java.security.MessageDigest
import java.util.*
import matt.kotlin.blockchain.ImmutableByteArray.Companion.digest
import matt.kotlin.blockchain.ImmutableByteArray.Companion.update
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream

class Blockchain<T: Serializable>
{
    var head: Block<T>? = null
        private set
    
    fun addBlock(data: T)
    {
        head = Block(head, data)
    }
}

class Block<T: Serializable>(val priorBlock: Block<T>?, val data: T)
{
    val priorHash: ImmutableByteArray = priorBlock?.hash() ?: ImmutableByteArray(MessageDigest.getInstance("SHA-256").digest())
    
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