package com.avocado.glampe_mobile.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.adapter.CampSiteAdapter;
import com.avocado.glampe_mobile.model.resp.CampSiteResponse;
import com.avocado.glampe_mobile.model.resp.CampTypeResponse;
import com.avocado.glampe_mobile.model.resp.FacilityResponse;
import com.avocado.glampe_mobile.model.resp.ImageResponse;
import com.avocado.glampe_mobile.model.resp.PlaceTypeResponse;
import com.avocado.glampe_mobile.model.resp.SelectionResponse;
import com.avocado.glampe_mobile.model.resp.UserResponse;
import com.avocado.glampe_mobile.model.resp.UtilityResponse;
import com.avocado.glampe_mobile.viewModel.BottomNavViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CampSiteFragment extends Fragment {

    private RecyclerView recyclerView;
    private BottomNavViewModel bottomNavViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camp_site, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomNavViewModel = new ViewModelProvider(requireActivity()).get(BottomNavViewModel.class);
        recyclerView = view.findViewById(R.id.recyclerView);
        NestedScrollView nestedScrollView = view.findViewById(R.id.nestedScrollView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        List<CampSiteResponse> campSiteResponses = createMoreCampSites();
        CampSiteAdapter campSiteAdapter = new CampSiteAdapter(campSiteResponses);
        recyclerView.setAdapter(campSiteAdapter);
        setupNestedScrollListener(nestedScrollView);
    }

    private void setupNestedScrollListener(NestedScrollView nestedScrollView) {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            private int lastScrollY = 0;
            private static final int SCROLL_THRESHOLD = 20;

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int deltaY = scrollY - lastScrollY;

                Log.d("ScrollDebug", "NestedScrollView - scrollY: " + scrollY + ", oldScrollY: " + oldScrollY + ", deltaY: " + deltaY);

                // Only respond to significant scroll changes
                if (Math.abs(deltaY) > SCROLL_THRESHOLD) {
                    if (deltaY > 0) {
                        Log.d("ScrollDebug", "Scrolling DOWN - hiding nav");
                        bottomNavViewModel.hideBottomNav();
                    } else {
                        Log.d("ScrollDebug", "Scrolling UP - showing nav");
                        bottomNavViewModel.showBottomNav();
                    }
                    lastScrollY = scrollY;
                }
            }
        });

        // Debug info
        nestedScrollView.post(() -> {
            Log.d("ScrollDebug", "NestedScrollView height: " + nestedScrollView.getHeight());
            Log.d("ScrollDebug", "NestedScrollView child height: " + nestedScrollView.getChildAt(0).getHeight());
            Log.d("ScrollDebug", "Can scroll down: " + nestedScrollView.canScrollVertically(1));
        });
    }



    public static List<CampSiteResponse> createCampSiteList() {
        List<CampSiteResponse> campSites = new ArrayList<>();

        // Camp Site 1 - Ba Vì Mountain Camp
        List<ImageResponse> images1 = Arrays.asList(
                ImageResponse.builder().id(1).path("/images/bavi_1.jpg").build(),
                ImageResponse.builder().id(2).path("/images/bavi_2.jpg").build()
        );

        List<SelectionResponse> selections1 = Arrays.asList(
                SelectionResponse.builder()
                        .id(1).name("Gói cơ bản").description("Bao gồm lều và chăn ga")
                        .price(500000.0).image("/images/basic_package.jpg").status(true).build(),
                SelectionResponse.builder()
                        .id(2).name("Gói VIP").description("Bao gồm lều, chăn ga, và BBQ")
                        .price(800000.0).image("/images/vip_package.jpg").status(true).build()
        );

        List<PlaceTypeResponse> placeTypes1 = Arrays.asList(
                PlaceTypeResponse.builder().id(1).name("Núi").imagePath("/images/mountain.jpg").status(true).build(),
                PlaceTypeResponse.builder().id(2).name("Rừng").imagePath("/images/forest.jpg").status(true).build()
        );

        List<UtilityResponse> utilities1 = Arrays.asList(
                UtilityResponse.builder().id(1).name("Wifi").imagePath("/images/wifi.jpg").status(true).build(),
                UtilityResponse.builder().id(2).name("Điện").imagePath("/images/power.jpg").status(true).build(),
                UtilityResponse.builder().id(3).name("Nước sạch").imagePath("/images/water.jpg").status(true).build()
        );

        List<FacilityResponse> facilities1 = Arrays.asList(
                FacilityResponse.builder().id(1).name("Lửa trại").description("Khu vực lửa trại an toàn")
                        .image("/images/campfire.jpg").status(true).build(),
                FacilityResponse.builder().id(2).name("Nhà vệ sinh").description("Nhà vệ sinh chung sạch sẽ")
                        .image("/images/toilet.jpg").status(true).build()
        );

        List<CampTypeResponse> campTypes1 = Arrays.asList(
                CampTypeResponse.builder()
                        .id(1).type("Lều 2 người").capacity(2).price(300000.0)
                        .weekendRate(1.5).updatedAt("2024-01-15").quantity(5)
                        .status(true).campSiteId(1).image("/images/tent_2p.jpg")
                        .facilities(facilities1).build(),
                CampTypeResponse.builder()
                        .id(2).type("Lều 4 người").capacity(4).price(500000.0)
                        .weekendRate(1.5).updatedAt("2024-01-15").quantity(3)
                        .status(true).campSiteId(1).image("/images/tent_4p.jpg")
                        .facilities(facilities1).build()
        );

        UserResponse user1 = UserResponse.builder()
                .id(1).firstName("Nguyễn Văn A").email("vana@gmail.com").build();

        CampSiteResponse campSite1 = CampSiteResponse.builder()
                .id(1).name("Ba Vì Mountain Camp")
                .address("Xã Tản Lĩnh, Huyện Ba Vì")
                .city("Hà Nội")
                .latitude(21.1167).longitude(105.3833)
                .createdTime("2024-01-01")
                .status("ACTIVE")
                .description("Khu cắm trại tuyệt đẹp trên núi Ba Vì với không khí trong lành và phong cảnh hùng vĩ")
                .depositRate(0.3)
                .user(user1)
                .imageList(images1)
                .campSiteSelectionList(selections1)
                .campSitePlaceTypeList(placeTypes1)
                .campSiteUtilityList(utilities1)
                .campSiteCampTypeList(campTypes1)
                .build();

        // Camp Site 2 - Sóc Sơn Lake Camp
        List<ImageResponse> images2 = Arrays.asList(
                ImageResponse.builder().id(3).path("/images/socson_1.jpg").build(),
                ImageResponse.builder().id(4).path("/images/socson_2.jpg").build(),
                ImageResponse.builder().id(5).path("/images/socson_3.jpg").build()
        );

        List<SelectionResponse> selections2 = Arrays.asList(
                SelectionResponse.builder()
                        .id(3).name("Gói gia đình").description("Phù hợp cho gia đình 4-6 người")
                        .price(1200000.0).image("/images/family_package.jpg").status(true).build()
        );

        List<PlaceTypeResponse> placeTypes2 = Arrays.asList(
                PlaceTypeResponse.builder().id(3).name("Hồ").imagePath("/images/lake.jpg").status(true).build(),
                PlaceTypeResponse.builder().id(4).name("Đồng cỏ").imagePath("/images/grassland.jpg").status(true).build()
        );

        List<UtilityResponse> utilities2 = Arrays.asList(
                UtilityResponse.builder().id(4).name("Bãi đỗ xe").imagePath("/images/parking.jpg").status(true).build(),
                UtilityResponse.builder().id(5).name("Khu BBQ").imagePath("/images/bbq.jpg").status(true).build(),
                UtilityResponse.builder().id(6).name("Cho thuê đồ cắm trại").imagePath("/images/rental.jpg").status(true).build()
        );

        List<FacilityResponse> facilities2 = Arrays.asList(
                FacilityResponse.builder().id(3).name("Bến thuyền").description("Cho thuê thuyền kayak")
                        .image("/images/boat_dock.jpg").status(true).build(),
                FacilityResponse.builder().id(4).name("Khu vui chơi trẻ em").description("Sân chơi an toàn cho trẻ em")
                        .image("/images/playground.jpg").status(true).build()
        );

        List<CampTypeResponse> campTypes2 = Arrays.asList(
                CampTypeResponse.builder()
                        .id(3).type("Bungalow").capacity(6).price(800000.0)
                        .weekendRate(1.8).updatedAt("2024-01-10").quantity(4)
                        .status(true).campSiteId(2).image("/images/bungalow.jpg")
                        .facilities(facilities2).build()
        );

        UserResponse user2 = UserResponse.builder()
                .id(2).firstName("Trần Thị B").email("thib@gmail.com").build();

        CampSiteResponse campSite2 = CampSiteResponse.builder()
                .id(2).name("Sóc Sơn Lake Camp")
                .address("Xã Hiền Ninh, Huyện Sóc Sơn")
                .city("Hà Nội")
                .latitude(21.2500).longitude(105.8333)
                .createdTime("2024-01-05")
                .status("ACTIVE")
                .description("Khu cắm trại bên hồ với nhiều hoạt động thú vị cho cả gia đình")
                .depositRate(0.25)
                .user(user2)
                .imageList(images2)
                .campSiteSelectionList(selections2)
                .campSitePlaceTypeList(placeTypes2)
                .campSiteUtilityList(utilities2)
                .campSiteCampTypeList(campTypes2)
                .build();

        // Camp Site 3 - Tam Đảo Forest Camp
        List<ImageResponse> images3 = Arrays.asList(
                ImageResponse.builder().id(6).path("/images/tamdao_1.jpg").build(),
                ImageResponse.builder().id(7).path("/images/tamdao_2.jpg").build()
        );

        List<SelectionResponse> selections3 = Arrays.asList(
                SelectionResponse.builder()
                        .id(4).name("Gói phiêu lưu").description("Trekking và cắm trại qua đêm")
                        .price(600000.0).image("/images/adventure_package.jpg").status(true).build()
        );

        List<PlaceTypeResponse> placeTypes3 = Arrays.asList(
                PlaceTypeResponse.builder().id(1).name("Núi").imagePath("/images/mountain.jpg").status(true).build(),
                PlaceTypeResponse.builder().id(2).name("Rừng").imagePath("/images/forest.jpg").status(true).build()
        );

        List<UtilityResponse> utilities3 = Arrays.asList(
                UtilityResponse.builder().id(7).name("Hướng dẫn viên").imagePath("/images/guide.jpg").status(true).build(),
                UtilityResponse.builder().id(8).name("Đồ dùng trekking").imagePath("/images/trekking_gear.jpg").status(true).build()
        );

        List<FacilityResponse> facilities3 = Arrays.asList(
                FacilityResponse.builder().id(5).name("Đường mòn trekking").description("Nhiều tuyến đường khác độ khó")
                        .image("/images/trail.jpg").status(true).build()
        );

        List<CampTypeResponse> campTypes3 = Arrays.asList(
                CampTypeResponse.builder()
                        .id(4).type("Lều trekking").capacity(2).price(400000.0)
                        .weekendRate(1.3).updatedAt("2024-01-12").quantity(8)
                        .status(true).campSiteId(3).image("/images/trekking_tent.jpg")
                        .facilities(facilities3).build()
        );

        UserResponse user3 = UserResponse.builder()
                .id(3).firstName("Lê Văn C").email("vanc@gmail.com").build();

        CampSiteResponse campSite3 = CampSiteResponse.builder()
                .id(3).name("Tam Đảo Forest Camp")
                .address("Tam Đảo, Vĩnh Phúc")
                .city("Vĩnh Phúc")
                .latitude(21.4667).longitude(105.6333)
                .createdTime("2024-01-08")
                .status("ACTIVE")
                .description("Trải nghiệm cắm trại giữa rừng nguyên sinh với hoạt động trekking thú vị")
                .depositRate(0.4)
                .user(user3)
                .imageList(images3)
                .campSiteSelectionList(selections3)
                .campSitePlaceTypeList(placeTypes3)
                .campSiteUtilityList(utilities3)
                .campSiteCampTypeList(campTypes3)
                .build();

        campSites.add(campSite1);
        campSites.add(campSite2);
        campSites.add(campSite3);

        return campSites;
    }

    private List<CampSiteResponse> createMoreCampSites() {
        List<CampSiteResponse> allCampSites = new ArrayList<>();

        // Get original 3 items
        List<CampSiteResponse> originalList = createCampSiteList();

        // Duplicate nhiều lần để có đủ content scroll
        for (int i = 0; i < 10; i++) { // Tạo 30 items (3 x 10)
            for (CampSiteResponse original : originalList) {
                CampSiteResponse duplicate = CampSiteResponse.builder()
                        .id(original.getId() + (i * 100)) // Unique ID
                        .name(original.getName() + " #" + (i + 1))
                        .address(original.getAddress())
                        .city(original.getCity())
                        .latitude(original.getLatitude())
                        .longitude(original.getLongitude())
                        .createdTime(original.getCreatedTime())
                        .status(original.getStatus())
                        .description(original.getDescription())
                        .depositRate(original.getDepositRate())
                        .user(original.getUser())
                        .imageList(original.getImageList())
                        .campSiteSelectionList(original.getCampSiteSelectionList())
                        .campSitePlaceTypeList(original.getCampSitePlaceTypeList())
                        .campSiteUtilityList(original.getCampSiteUtilityList())
                        .campSiteCampTypeList(original.getCampSiteCampTypeList())
                        .build();

                allCampSites.add(duplicate);
            }
        }

        Log.d("ScrollDebug", "Created " + allCampSites.size() + " items for testing");
        return allCampSites;
    }
}
