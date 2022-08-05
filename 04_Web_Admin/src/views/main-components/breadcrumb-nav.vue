<template>
    <Breadcrumb>
        <BreadcrumbItem
            v-for="item in currentPath"
            :href="item.path"
            :key="item.name"
        >{{ $t(`app.${itemTitle(item)}`) }}</BreadcrumbItem>
    </Breadcrumb>
</template>

<script>
export default {
    name: 'breadcrumbNav',
    props: {
        currentPath: Array
    },
    methods: {
        itemTitle (item) {
            if (typeof item.titleKey === 'object') {
                return this.$t(item.titleKey.i18n);
            } else {
                return item.titleKey;
            }
        }
    }
};
</script>

