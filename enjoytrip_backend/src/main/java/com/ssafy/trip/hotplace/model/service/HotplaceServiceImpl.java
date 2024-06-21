package com.ssafy.trip.hotplace.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.trip.common.ImgUtils;
import com.ssafy.trip.exception.InvalidInputException;
import com.ssafy.trip.exception.ResourceNotFoundException;
import com.ssafy.trip.exception.util.BaseResponseCode;
import com.ssafy.trip.hotplace.model.HotplaceDto;
import com.ssafy.trip.hotplace.model.HotplaceLikeDto;
import com.ssafy.trip.hotplace.model.WriteHotplaceRequest;
import com.ssafy.trip.hotplace.model.mapper.HotplaceMapper;
import com.ssafy.trip.user.model.service.UserService;

@Service
public class HotplaceServiceImpl implements HotplaceService {

	private final HotplaceMapper hotplaceMapper;
	private final UserService userService;
	private final ImgUtils imgUtils;
	private static final String HOTPLACE_PATH = "hotplace";

	public HotplaceServiceImpl(HotplaceMapper hotplaceMapper, UserService userService, ImgUtils imgUtils) {
		this.hotplaceMapper = hotplaceMapper;
		this.userService = userService;
		this.imgUtils = imgUtils;
	}

	@Override
	@Transactional
	public void insertHotplace(WriteHotplaceRequest hotplace, MultipartFile img) {
		String imgPath = imgUtils.saveImage(img, HOTPLACE_PATH);
		int result = hotplaceMapper.insertHotplace(HotplaceDto.builder()
				.title(hotplace.getTitle())
				.img(imgPath)
				.description(hotplace.getDescription())
				.placeName(hotplace.getPlaceName())
				.latitude(hotplace.getLatitude())
				.longitude(hotplace.getLongitude())
				.userId(userService.getIdByUserId(hotplace.getUserId()))
				.build());
		if (result == 0) {
			throw new InvalidInputException(BaseResponseCode.DATABASE_REQUEST_FAILED);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<HotplaceDto> listHotplaces() {
		List<HotplaceDto> list = hotplaceMapper.listHotplaces();
		if (list == null || list.size() == 0) {
			throw new ResourceNotFoundException(BaseResponseCode.RESOURCE_NOT_FOUND);
		} else {
			return list;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<HotplaceDto> userHotplaces(int id) {
		List<HotplaceDto> list = hotplaceMapper.userHotplaces(id);
		if (list == null || list.size() == 0) {
			throw new ResourceNotFoundException(BaseResponseCode.RESOURCE_NOT_FOUND);
		} else {
			return list;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public HotplaceDto getHotplace(int id) {
		HotplaceDto hotplace = hotplaceMapper.getHotplace(id);
		if (hotplace == null) {
			throw new ResourceNotFoundException(BaseResponseCode.RESOURCE_NOT_FOUND);
		} else {
			return hotplace;
		}
	}

	@Override
	@Transactional
	public void updateHotplace(int id, HotplaceDto hotplace, MultipartFile img) {
		String originPath = "";
		if (img != null && !img.isEmpty()) {
			originPath = hotplaceMapper.getImgById(hotplace.getId());
			if (originPath != null && !originPath.isEmpty()) {
				imgUtils.deleteImage(originPath, HOTPLACE_PATH);
			}
			String imgPath = imgUtils.saveImage(img, HOTPLACE_PATH);
			hotplace.setImg(imgPath);
		}
		int result = hotplaceMapper.updateHotplace(hotplace);
		if (result == 0) {
			throw new InvalidInputException(BaseResponseCode.DATABASE_REQUEST_FAILED);
		}
	}

	@Override
	@Transactional
	public void deleteHotplace(int id) {
		String originPath = hotplaceMapper.getImgById(id);
		int result = hotplaceMapper.deleteHotplace(id);
		if (result == 0) {
			throw new InvalidInputException(BaseResponseCode.DATABASE_REQUEST_FAILED);
		}
		if (originPath != null && !originPath.isEmpty()) {
			imgUtils.deleteImage(originPath, HOTPLACE_PATH);
		}
	}

	@Override
	@Transactional
	public void plusLikeCount(int id) {
		int result = hotplaceMapper.plusLikeCount(id);
		if (result == 0) {
			throw new InvalidInputException(BaseResponseCode.DATABASE_REQUEST_FAILED);
		}
	}

	@Override
	@Transactional
	public void minusLikeCount(int id) {
		int result = hotplaceMapper.minusLikeCount(id);
		if (result == 0) {
			throw new InvalidInputException(BaseResponseCode.DATABASE_REQUEST_FAILED);
		}
	}

	@Override
	@Transactional
	public void like(HotplaceLikeDto hotplaceLike) {
		Integer result = hotplaceMapper.getLike(hotplaceLike);
		if (result == null) {
			//없으면 진행
			try {
				hotplaceMapper.addLike(hotplaceLike);
				hotplaceMapper.plusLikeCount(hotplaceLike.getHotplacesId());
			} catch (Exception e) {
				throw new InvalidInputException(BaseResponseCode.DATABASE_REQUEST_FAILED);
			}
		} else {
			throw new InvalidInputException(BaseResponseCode.DATABASE_REQUEST_FAILED);
		}
	}

	@Override
	@Transactional
	public void unlike(HotplaceLikeDto hotplaceLike) {
		Integer result = hotplaceMapper.getLike(hotplaceLike);
		if (result != null) {
			//있으면 진행
			try {
				hotplaceMapper.deleteLike(hotplaceLike);
				hotplaceMapper.minusLikeCount(hotplaceLike.getHotplacesId());
			} catch (Exception e) {
				throw new InvalidInputException(BaseResponseCode.DATABASE_REQUEST_FAILED);
			}
		} else {
			throw new InvalidInputException(BaseResponseCode.DATABASE_REQUEST_FAILED);
		}
	}

	
}
