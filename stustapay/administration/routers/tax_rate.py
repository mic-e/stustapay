from fastapi import APIRouter, Depends, status, HTTPException

from stustapay.core.http.auth_user import get_current_user
from stustapay.core.http.context import get_tax_rate_service
from stustapay.core.schema.tax_rate import TaxRate, TaxRateWithoutName
from stustapay.core.schema.user import User
from stustapay.core.service.tax_rate import TaxRateService

router = APIRouter(
    prefix="/tax-rates",
    tags=["tax-rates"],
    responses={404: {"description": "Not found"}},
)


@router.get("/", response_model=list[TaxRate])
async def list_tax_rates(
    user: User = Depends(get_current_user), tax_service: TaxRateService = Depends(get_tax_rate_service)
):
    return await tax_service.list_tax_rates(user=user)


@router.post("/", response_model=TaxRate)
async def create_tax_rate(
    tax_rate: TaxRate,
    user: User = Depends(get_current_user),
    tax_service: TaxRateService = Depends(get_tax_rate_service),
):
    return await tax_service.create_tax_rate(user=user, tax_rate=tax_rate)


@router.get("/{tax_rate_name}", response_model=TaxRate)
async def get_tax_rate(
    tax_rate_name: str,
    user: User = Depends(get_current_user),
    tax_service: TaxRateService = Depends(get_tax_rate_service),
):
    tax_rate = await tax_service.get_tax_rate(user=user, tax_rate_name=tax_rate_name)
    if tax_rate is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND)

    return tax_rate


@router.post("/{tax_rate_name}", response_model=TaxRate)
async def update_tax_rate(
    tax_rate_name: str,
    tax_rate: TaxRateWithoutName,
    user: User = Depends(get_current_user),
    tax_service: TaxRateService = Depends(get_tax_rate_service),
):
    tax_rate = await tax_service.update_tax_rate(user=user, tax_rate_name=tax_rate_name, tax_rate=tax_rate)
    if tax_rate is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND)

    return tax_rate


@router.delete("/{tax_rate_name}")
async def delete_tax_rate(
    tax_rate_name: str,
    user: User = Depends(get_current_user),
    tax_service: TaxRateService = Depends(get_tax_rate_service),
):
    deleted = await tax_service.delete_tax_rate(user=user, tax_rate_name=tax_rate_name)
    if not deleted:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND)
