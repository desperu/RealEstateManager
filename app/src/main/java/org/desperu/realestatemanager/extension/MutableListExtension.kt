package org.desperu.realestatemanager.extension

/**
 * Delete all item in mutable list up to 3.
 * @param T all MutableList types.
 * @return the cleaned up to 3 mutable list, if not null.
 */
fun<T: MutableList<*>?> T.deleteUpTo3(): T {
    if (this != null) {
        if (this.size > 3)
            do {
                this.removeAt(this.size - 1)
            } while (this.size > 3)
    }

    return this
}