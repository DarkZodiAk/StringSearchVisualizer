package util

import boyermoore.BMViewModel
import bruteforce.BFViewModel
import kmp.KMPViewModel
import rabin_karp.RKViewModel

object ViewModels {
    val bfViewModel by lazy { BFViewModel() }
    val rkViewModel by lazy { RKViewModel() }
    val kmpViewModel by lazy { KMPViewModel() }
    val bmViewModel by lazy { BMViewModel() }
}