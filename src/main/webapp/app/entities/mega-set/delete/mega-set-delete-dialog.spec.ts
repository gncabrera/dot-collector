import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { of } from 'rxjs';

import { MegaSetService } from '../service/mega-set.service';

import { MegaSetDeleteDialog } from './mega-set-delete-dialog';

describe('MegaSet Management Delete Component', () => {
  let comp: MegaSetDeleteDialog;
  let fixture: ComponentFixture<MegaSetDeleteDialog>;
  let service: MegaSetService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [NgbActiveModal],
    });
    fixture = TestBed.createComponent(MegaSetDeleteDialog);
    comp = fixture.componentInstance;
    service = TestBed.inject(MegaSetService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('should call delete service on confirmDelete', () => {
      // GIVEN
      vitest.spyOn(service, 'delete').mockReturnValue(of(undefined));
      vitest.spyOn(mockActiveModal, 'close');

      // WHEN
      comp.confirmDelete(123);

      // THEN
      expect(service.delete).toHaveBeenCalledWith(123);
      expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
    });

    it('should not call delete service on clear', () => {
      // GIVEN
      vitest.spyOn(service, 'delete');
      vitest.spyOn(mockActiveModal, 'close');
      vitest.spyOn(mockActiveModal, 'dismiss');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
