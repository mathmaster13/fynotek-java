package com.mathmaster13.fynotek

operator fun FynotekParent.component1() = this.beginning
operator fun FynotekParent.component2() = this.vowels
operator fun FynotekParent.component3() = this.end

operator fun FynotekParent.get(index: Int) = this.toString()[index]