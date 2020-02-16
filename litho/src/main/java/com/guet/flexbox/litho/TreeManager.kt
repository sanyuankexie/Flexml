@file:Suppress("PackageDirectoryMismatch")

package com.facebook.litho

import androidx.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY)
abstract class TreeManager internal constructor(
        builder: Builder?
) : ComponentTree(builder) {

    override fun attach() {
        super.attach()
    }

    override fun detach() {
        super.detach()
    }
}