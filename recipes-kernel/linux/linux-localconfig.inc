# Copyright (C) 2019 Muuselabs NV
#
# copy additional device tree sources from the yocto recipe

do_configure_prepend() {
    for filename in ${WORKDIR}/${MACHINE}/*.dts*; do
        [ -e "$filename" ] || continue
        cp $filename ${S}/arch/mips/boot/dts/ingenic/
    done
    if [ -e "${WORKDIR}/${MACHINE}/defconfig" ]; then
        cp -f ${WORKDIR}/${MACHINE}/defconfig ${WORKDIR}
    fi
}
